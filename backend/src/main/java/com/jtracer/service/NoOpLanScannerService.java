package com.jtracer.service;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.stereotype.Service;

@Service
@ConditionalOnMissingBean(LanScannerService.class)
public class NoOpLanScannerService implements LanScannerService {

    @Override
    public void runScan() {
        // LAN scanning is only available on supported host platforms.
    }
}
