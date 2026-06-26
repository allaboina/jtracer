package com.jtracer.collector.common;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.Test;

class OuiVendorLookupTest {

    @Test
    void extractsOuiPrefixFromColonSeparatedMac() {
        assertEquals("fc:a6:67", OuiVendorLookup.extractOuiPrefix("FC:A6:67:11:22:33"));
    }

    @Test
    void looksUpVendorByMacPrefix() {
        OuiVendorLookup lookup = new OuiVendorLookup(java.util.Map.of("fc:a6:67", "Amazon Technologies Inc."));
        assertEquals("Amazon Technologies Inc.", lookup.lookupVendor("fc:a6:67:11:22:33"));
        assertNull(lookup.lookupVendor("00:00:00:00:00:00"));
    }
}
