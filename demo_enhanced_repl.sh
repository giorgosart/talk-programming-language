#!/bin/bash

# Demo script for the enhanced Talk REPL with JLine support

# Set colors
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
PURPLE='\033[0;35m'
NC='\033[0m' # No Color

# Display header
echo -e "${BLUE}=======================================================${NC}"
echo -e "${PURPLE}       Talk Programming Language - Enhanced REPL       ${NC}"
echo -e "${BLUE}=======================================================${NC}"
echo

# Check if enhanced_repl.sh exists and is executable
if [ ! -x "./enhanced_repl.sh" ]; then
    echo -e "${YELLOW}Making enhanced_repl.sh executable...${NC}"
    chmod +x ./enhanced_repl.sh
fi

# Show instructions
echo -e "${GREEN}This demo will launch the enhanced REPL with:${NC}"
echo -e "  • ${YELLOW}Arrow key navigation${NC} through command history"
echo -e "  • ${YELLOW}Tab completion${NC} for keywords and variables"
echo -e "  • ${YELLOW}Persistent history${NC} between sessions"
echo -e "  • ${YELLOW}Ctrl+R${NC} for history search"
echo

# Show some example commands
echo -e "${GREEN}Try these commands:${NC}"
echo -e "  ${BLUE}write \"Hello, world!\"${NC}"
echo -e "  ${BLUE}variable name = \"Talk\"${NC}"
echo -e "  ${BLUE}write \"I love \" + name${NC}"
echo -e "  ${BLUE}10 + 20 * 3${NC}"
echo
echo -e "${GREEN}Keyboard shortcuts:${NC}"
echo -e "  ${BLUE}↑/↓${NC} - Navigate through history"
echo -e "  ${BLUE}Tab${NC} - Autocomplete (try typing 'va' then Tab)"
echo -e "  ${BLUE}Ctrl+R${NC} - Search history"
echo -e "  ${BLUE}Ctrl+A/E${NC} - Jump to beginning/end of line"
echo -e "  ${BLUE}Ctrl+D${NC} - Exit the REPL"
echo

# Prompt to launch
echo -e "${YELLOW}Press Enter to launch the enhanced REPL...${NC}"
read

# Run the enhanced REPL
./enhanced_repl.sh
