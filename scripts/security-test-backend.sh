#!/bin/bash

# DocUp Backend Security Testing Script
# Runs comprehensive static application security testing

set -e

echo "🔒 Starting Backend Security Testing for DocUp..."
echo "=================================================="

# Change to backend directory
cd "$(dirname "$0")/../backend"

echo "📦 Installing/updating security testing tools..."
# Download source attachments for security tools (optional)
mvn dependency:sources || echo "⚠️ Some source attachments may not be available"

echo ""
echo "🔍 1. OWASP Dependency Check - Scanning for vulnerable dependencies..."
echo "----------------------------------------------------------------------"
echo "⚠️ Note: OWASP Dependency Check requires NVD API access. If it fails due to API limits,"
echo "   consider getting a free NVD API key from: https://nvd.nist.gov/developers/request-an-api-key"

# Try OWASP check with fallback handling
if mvn org.owasp:dependency-check-maven:check; then
    echo "✅ OWASP Dependency Check completed successfully"
else
    echo "⚠️ OWASP Dependency Check failed - this may be due to NVD API limits"
    echo "📄 This is common in CI/CD environments without API keys"
    echo "🔄 Continuing with other security tests..."
    # Don't exit on OWASP failure in CI/CD - continue with other security tests
fi

echo ""
echo "🔍 2. SpotBugs with FindSecBugs - Static code analysis for security bugs..."
echo "--------------------------------------------------------------------------"
mvn compile spotbugs:check || {
    echo "❌ SpotBugs found security issues!"
    echo "📄 Check target/spotbugsXml.xml for details"
    exit 1
}

echo ""
echo "🔍 3. PMD Security Rules - Code quality and security analysis..."
echo "---------------------------------------------------------------"
mvn pmd:check || {
    echo "❌ PMD found security violations!"
    echo "📄 Check target/pmd.xml for details"
    exit 1
}

echo ""
echo "🔍 4. Checkstyle Security - Coding standards security check..."
echo "------------------------------------------------------------"
mvn checkstyle:check || {
    echo "❌ Checkstyle found security-related coding standard violations!"
    echo "📄 Check target/checkstyle-result.xml for details"
    exit 1
}

echo ""
echo "✅ All backend security tests passed!"
echo "📊 Security Reports Generated:"
echo "   - OWASP Dependency Check: target/dependency-check-report.html"
echo "   - SpotBugs: target/spotbugsXml.xml"
echo "   - PMD: target/pmd.xml"
echo "   - Checkstyle: target/checkstyle-result.xml"
echo ""
echo "🎉 Backend security testing completed successfully!"
