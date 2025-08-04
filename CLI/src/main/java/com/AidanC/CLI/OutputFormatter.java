package com.AidanC.CLI;

import org.fusesource.jansi.Ansi;
import org.springframework.stereotype.Component;

@Component
public class OutputFormatter {
    
    public OutputFormatter() {
        // Enable ANSI colors
        org.fusesource.jansi.AnsiConsole.systemInstall();
    }
    
    public String formatResponse(String response) {
        if (response == null || response.isEmpty()) {
            return response;
        }
        
        // Check if response contains markdown-like patterns
        if (containsMarkdown(response)) {
            return formatMarkdown(response);
        } else {
            return formatPlainText(response);
        }
    }
    
    private boolean containsMarkdown(String text) {
        // Simple check for common markdown patterns
        return text.contains("**") || text.contains("*") || 
               text.contains("#") || text.contains("```") ||
               text.contains("- ") || text.contains("1. ");
    }
    
    private String formatMarkdown(String markdown) {
        StringBuilder formatted = new StringBuilder();
        String[] lines = markdown.split("\n");
        
        boolean inCodeBlock = false;
        
        for (String line : lines) {
            if (line.trim().startsWith("```")) {
                inCodeBlock = !inCodeBlock;
                if (inCodeBlock) {
                    formatted.append(Ansi.ansi().fgBrightBlack().a("┌─── Code ───").reset().a("\n"));
                } else {
                    formatted.append(Ansi.ansi().fgBrightBlack().a("└───────────").reset().a("\n"));
                }
                continue;
            }
            
            if (inCodeBlock) {
                formatted.append(Ansi.ansi().fgCyan().a("│ ").a(line).reset().a("\n"));
                continue;
            }
            
            // Headers
            if (line.startsWith("### ")) {
                formatted.append(Ansi.ansi().bold().fgBrightYellow().a("🔸 ")
                    .a(line.substring(4)).reset().a("\n"));
            } else if (line.startsWith("## ")) {
                formatted.append(Ansi.ansi().bold().fgBrightBlue().a("🔷 ")
                    .a(line.substring(3)).reset().a("\n"));
            } else if (line.startsWith("# ")) {
                formatted.append(Ansi.ansi().bold().fgBrightMagenta().a("🔻 ")
                    .a(line.substring(2)).reset().a("\n"));
            }
            // Bold text
            else if (line.contains("**")) {
                String boldFormatted = line.replaceAll("\\*\\*(.*?)\\*\\*", 
                    Ansi.ansi().bold().a("$1").reset().toString());
                formatted.append(boldFormatted).append("\n");
            }
            // Italic text (using underline since italic may not be supported)
            else if (line.contains("*") && !line.trim().startsWith("*")) {
                String italicFormatted = line.replaceAll("\\*(.*?)\\*", 
                    Ansi.ansi().a(Ansi.Attribute.UNDERLINE).a("$1").reset().toString());
                formatted.append(italicFormatted).append("\n");
            }
            // Lists
            else if (line.trim().startsWith("- ")) {
                formatted.append(Ansi.ansi().fgBrightGreen().a("  • ")
                    .reset().a(line.trim().substring(2))).append("\n");
            } else if (line.trim().matches("\\d+\\. .*")) {
                String number = line.trim().split("\\. ")[0];
                String content = line.trim().substring(number.length() + 2);
                formatted.append(Ansi.ansi().fgBrightGreen().a("  " + number + ". ")
                    .reset().a(content)).append("\n");
            }
            // Regular text
            else {
                formatted.append(line).append("\n");
            }
        }
        
        return formatted.toString().trim();
    }
    
    private String formatPlainText(String text) {
        // Add some basic formatting for plain text
        StringBuilder formatted = new StringBuilder();
        
        // Add a subtle border
        formatted.append(Ansi.ansi().fgBrightBlack().a("┌" + "─".repeat(50) + "┐").reset().a("\n"));
        
        String[] lines = text.split("\n");
        for (String line : lines) {
            formatted.append(Ansi.ansi().fgBrightBlack().a("│ ").reset())
                     .append(line)
                     .append(Ansi.ansi().fgBrightBlack().a(String.format("%" + (49 - line.length()) + "s│", "")).reset())
                     .append("\n");
        }
        
        formatted.append(Ansi.ansi().fgBrightBlack().a("└" + "─".repeat(50) + "┘").reset().a("\n"));
        
        return formatted.toString();
    }
    
    public String formatSuccess(String message) {
        return Ansi.ansi().fgBrightGreen().a("✅ ").a(message).reset().toString();
    }
    
    public String formatError(String message) {
        return Ansi.ansi().fgBrightRed().a("❌ ").a(message).reset().toString();
    }
    
    public String formatInfo(String message) {
        return Ansi.ansi().fgBrightBlue().a("ℹ️  ").a(message).reset().toString();
    }
    
    public String formatWarning(String message) {
        return Ansi.ansi().fgBrightYellow().a("⚠️  ").a(message).reset().toString();
    }
}