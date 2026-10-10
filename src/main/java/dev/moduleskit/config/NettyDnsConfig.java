package dev.moduleskit.config;

import jakarta.annotation.PostConstruct;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration to force IPv4-only DNS resolution on Netty.
 * Prevents "Network is unreachable" errors when IPv6 routes are blocked.
 * 
 * This must be initialized before any Netty DNS operations occur.
 */
@Configuration
@EnableConfigurationProperties
public class NettyDnsConfig {

    @PostConstruct
    public void configureNettyDns() {
        // Force IPv4 stack at the JVM level
        // This tells the JVM to prefer IPv4 addresses over IPv6
        System.setProperty("java.net.preferIPv4Stack", "true");
        
        // Configure Netty to use IPv4-aware DNS resolution
        System.setProperty("io.netty.resolver.addressResolverGroup.preferDirect", "true");
        System.setProperty("io.netty.noUnsafe", "false");
    }
}
