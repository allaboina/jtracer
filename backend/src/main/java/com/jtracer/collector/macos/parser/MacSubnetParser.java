package com.jtracer.collector.macos.parser;

import com.jtracer.dto.collector.SubnetInfoDto;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Parses macOS {@code ifconfig} and {@code route -n get default} output for subnet context.
 */
public final class MacSubnetParser {

    private static final Pattern ROUTE_INTERFACE = Pattern.compile("^\\s*interface:\\s*(\\S+)\\s*$");
    private static final Pattern ROUTE_GATEWAY = Pattern.compile("^\\s*gateway:\\s*(\\S+)\\s*$");
    private static final Pattern IFCONFIG_INET = Pattern.compile(
            "^\\s*inet\\s+(\\d+\\.\\d+\\.\\d+\\.\\d+)\\s+netmask\\s+(0x[0-9a-fA-F]+)");

    private MacSubnetParser() {
    }

    public static SubnetInfoDto parse(String routeOutput, String ifconfigOutput) {
        String adapter = parseRouteField(routeOutput, ROUTE_INTERFACE);
        String gateway = parseRouteField(routeOutput, ROUTE_GATEWAY);

        SubnetInfoDto subnet = parseIfconfig(ifconfigOutput);
        if (subnet == null) {
            subnet = new SubnetInfoDto();
        }
        if (adapter != null) {
            subnet.setAdapterName(adapter);
        }
        if (gateway != null) {
            subnet.setGateway(gateway);
        }
        if (subnet.getLocalIp() != null && subnet.getNetmask() != null && subnet.getSubnetCidr() == null) {
            subnet.setSubnetCidr(toCidr(subnet.getLocalIp(), subnet.getNetmask()));
        }
        return subnet;
    }

    static SubnetInfoDto parseIfconfig(String ifconfigOutput) {
        if (ifconfigOutput == null || ifconfigOutput.isBlank()) {
            return null;
        }
        for (String line : ifconfigOutput.split("\n")) {
            Matcher matcher = IFCONFIG_INET.matcher(line);
            if (matcher.find()) {
                SubnetInfoDto subnet = new SubnetInfoDto();
                subnet.setLocalIp(matcher.group(1));
                subnet.setNetmask(matcher.group(2));
                subnet.setSubnetCidr(toCidr(subnet.getLocalIp(), subnet.getNetmask()));
                return subnet;
            }
        }
        return null;
    }

    public static String parseRouteField(String routeOutput, Pattern pattern) {
        if (routeOutput == null) {
            return null;
        }
        for (String line : routeOutput.split("\n")) {
            Matcher matcher = pattern.matcher(line);
            if (matcher.matches()) {
                return matcher.group(1);
            }
        }
        return null;
    }

    static String toCidr(String ipAddress, String hexNetmask) {
        int prefix = netmaskHexToPrefix(hexNetmask);
        if (prefix <= 0 || prefix > 32) {
            return null;
        }
        String[] octets = ipAddress.split("\\.");
        if (octets.length != 4) {
            return null;
        }
        int ip = (Integer.parseInt(octets[0]) << 24)
                | (Integer.parseInt(octets[1]) << 16)
                | (Integer.parseInt(octets[2]) << 8)
                | Integer.parseInt(octets[3]);
        int mask = prefix == 0 ? 0 : 0xFFFFFFFF << (32 - prefix);
        int network = ip & mask;
        return String.format(
                        Locale.ROOT,
                        "%d.%d.%d.%d/%d",
                        (network >> 24) & 0xFF,
                        (network >> 16) & 0xFF,
                        (network >> 8) & 0xFF,
                        network & 0xFF,
                        prefix);
    }

    static int netmaskHexToPrefix(String hexNetmask) {
        String normalized = hexNetmask.toLowerCase(Locale.ROOT);
        if (normalized.startsWith("0x")) {
            normalized = normalized.substring(2);
        }
        long mask = Long.parseLong(normalized, 16);
        return Long.bitCount(mask);
    }
}
