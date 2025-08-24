#!/bin/bash

# DocUp Frontend Security Testing Script
# Runs comprehensive static application security testing for Angular

set -e

echo "🔒 Starting Frontend Security Testing for DocUp..."
echo "=================================================="

# Change to frontend directory
cd "$(dirname "$0")/../frontend"

echo "📦 Installing security testing dependencies..."
npm install --save-dev

echo ""
echo "🔍 1. ESLint Security - Static code analysis with security rules..."
echo "----------------------------------------------------------------"
npm run lint:security || {
    echo "❌ ESLint found security issues!"
    echo "💡 Fix the issues and run again"
    exit 1
}

echo ""
echo "🔍 2. NPM Audit CI - Dependency vulnerability scanning..."
echo "------------------------------------------------------"
npm run audit-ci || {
    echo "❌ NPM Audit found high/critical vulnerabilities!"
    echo "💡 Run 'npm audit fix' to attempt automatic fixes"
    exit 1
}

echo ""
echo "🔍 3. Retire.js - Scanning for known vulnerable JavaScript libraries..."
echo "--------------------------------------------------------------------"
npm run retire || {
    echo "⚠️  Retire.js found potentially vulnerable libraries"
    echo "📄 Check retire-report.json for details"
    # Don't exit on retire issues as they might be false positives
}

echo ""
echo "🔍 4. Building production bundle with security optimizations..."
echo "------------------------------------------------------------"
npm run build -- --configuration production || {
    echo "❌ Production build failed!"
    exit 1
}

echo ""
echo "✅ Frontend security tests completed!"
echo "📊 Security Reports Generated:"
echo "   - Retire.js: retire-report.json"
echo "   - Build output: dist/"
echo ""

# Optional: Run Lighthouse security audit if application is running
if curl -s http://localhost:4200 > /dev/null 2>&1; then
    echo "🔍 5. Lighthouse Security Audit - Performance and security analysis..."
    echo "-------------------------------------------------------------------"
    npm run lighthouse || {
        echo "⚠️  Lighthouse audit completed with issues"
        echo "📄 Check lighthouse-report.json for details"
    }
    echo "   - Lighthouse: lighthouse-report.json"
else
    echo "ℹ️  Skipping Lighthouse audit (application not running on localhost:4200)"
fi

echo ""
echo "🎉 Frontend security testing completed successfully!"
