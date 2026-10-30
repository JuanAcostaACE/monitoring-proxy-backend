package com.monitoring.system.service;

import java.util.Map;

public interface MicroserviceTarget {
    String getServiceName();
    Map<String, Object> executeRequest(String operation, Map<String, Object> payload);
}
