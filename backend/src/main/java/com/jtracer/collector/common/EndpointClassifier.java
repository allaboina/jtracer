package com.jtracer.collector.common;

import com.jtracer.domain.enums.EndpointType;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.UnknownHostException;

public final class EndpointClassifier {

    private EndpointClassifier() {
    }

    public static EndpointType classify(String ipAddress) {
        if (ipAddress == null || ipAddress.isBlank()) {
            return EndpointType.UNKNOWN;
        }
        if ("127.0.0.1".equals(ipAddress) || "::1".equals(ipAddress) || ipAddress.startsWith("127.")) {
            return EndpointType.LOOPBACK;
        }
        if (ipAddress.startsWith("224.") || ipAddress.startsWith("ff")) {
            return EndpointType.MULTICAST;
        }
        try {
            InetAddress address = InetAddress.getByName(ipAddress);
            if (address.isLoopbackAddress()) {
                return EndpointType.LOOPBACK;
            }
            if (address.isMulticastAddress()) {
                return EndpointType.MULTICAST;
            }
            if (address.isLinkLocalAddress()) {
                return EndpointType.LAN_DEVICE;
            }
            if (address instanceof Inet6Address) {
                return isPrivateIpv6(ipAddress) ? EndpointType.LAN_DEVICE : EndpointType.PUBLIC_INTERNET;
            }
            if (isPrivateIpv4(ipAddress)) {
                return EndpointType.LAN_DEVICE;
            }
            return EndpointType.PUBLIC_INTERNET;
        } catch (UnknownHostException e) {
            return EndpointType.UNKNOWN;
        }
    }

    private static boolean isPrivateIpv4(String ipAddress) {
        return ipAddress.startsWith("10.")
                || ipAddress.startsWith("192.168.")
                || ipAddress.matches("172\\.(1[6-9]|2\\d|3[0-1])\\..*");
    }

    private static boolean isPrivateIpv6(String ipAddress) {
        String normalized = ipAddress.toLowerCase();
        return normalized.startsWith("fc") || normalized.startsWith("fd");
    }
}
