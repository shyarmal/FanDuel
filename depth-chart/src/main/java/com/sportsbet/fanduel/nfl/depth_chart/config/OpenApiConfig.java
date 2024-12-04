package com.sportsbet.fanduel.nfl.depth_chart.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;

@OpenAPIDefinition(
        info = @Info(
                contact = @Contact(
                        name = "Danuka"
                ),
                description = "NFL Depth Chart API",
                title = "NFL Depth Chart Specification",
                version = "1.0"
        )
)
public class OpenApiConfig {
}
