package com.osrsflip.model.dto;

public record ItemMappingDto(
        int id,
        String name,
        int limit,
        boolean members,
        int lowalch,
        int highalch,
        int value,
        String icon,
        String examine
) {}
