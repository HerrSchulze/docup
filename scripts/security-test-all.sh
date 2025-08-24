#!/bin/bash

# DocUp Complete Security Testing Suite
# Runs all security tests for both frontend and backend

set -e

echo "🛡️  DocUp Complete Security Testing Suite"
echo "========================================"
echo "Running comprehensive security analysis for the entire application"
echo ""

# Get the script directory
SCRIPT_DIR="$(dirname "$0")"

# Run backend security tests
echo "🔐 PHASE 1: Backend Security Testing"
echo "===================================="
"$SCRIPT_DIR/security-test-backend.sh"

echo ""
echo ""

# Run frontend security tests  
echo "🔐 PHASE 2: Frontend Security Testing"
echo "====================================="
"$SCRIPT_DIR/security-test-frontend.sh"

echo ""
echo ""

# Generate summary report
echo "📋 SECURITY TESTING SUMMARY"
echo "==========================="
echo "✅ Backend Security Tests: PASSED"
echo "✅ Frontend Security Tests: PASSED"
echo ""
echo "📊 Generated Security Reports:"
echo ""
echo "Backend Reports:"
echo "   📄 OWASP Dependency Check: backend/target/dependency-check-report.html"
echo "   📄 SpotBugs Security: backend/target/spotbugsXml.xml"
echo "   📄 PMD Security: backend/target/pmd.xml"
echo "   📄 Checkstyle: backend/target/checkstyle-result.xml"
echo ""
echo "Frontend Reports:"
echo "   📄 Retire.js: frontend/retire-report.json"
echo "   📄 Lighthouse: frontend/lighthouse-report.json (if app was running)"
echo ""
echo "🎯 Security Recommendations:"
echo "   1. Review all generated reports for potential issues"
echo "   2. Set up automated security testing in CI/CD pipeline"
echo "   3. Run security tests before each release"
echo "   4. Keep dependencies updated regularly"
echo "   5. Implement security headers in production"
echo ""
echo "🎉 Complete security testing suite finished successfully!"
