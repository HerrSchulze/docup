# DocUp Security Testing Guide

## Overview

DocUp implements comprehensive static application security testing (SAST) to ensure the security and reliability of both frontend and backend components. This document outlines the security testing tools, processes, and best practices implemented in the project.

## 🛡️ Security Testing Tools

### Backend Security (Java/Spring Boot)

#### 1. OWASP Dependency Check
- **Purpose**: Identifies known vulnerabilities in project dependencies
- **Configuration**: `backend/owasp-suppressions.xml`
- **Threshold**: Fails build on CVSS score ≥ 7.0
- **Reports**: `backend/target/dependency-check-report.html`

#### 2. SpotBugs with FindSecBugs
- **Purpose**: Static analysis for security bugs and vulnerabilities
- **Configuration**: `backend/spotbugs-security-include.xml`
- **Focus Areas**:
  - SQL Injection vulnerabilities
  - Path traversal attacks
  - Command injection
  - XSS vulnerabilities
  - Cryptographic weaknesses
  - Trust boundary violations

#### 3. PMD Security Rules
- **Purpose**: Code quality and security analysis
- **Configuration**: `backend/pmd-security-rules.xml`
- **Features**:
  - Security-focused rule sets
  - Custom file handling security rules
  - Error handling best practices

#### 4. Checkstyle Security
- **Purpose**: Coding standards that enhance security
- **Configuration**: `backend/checkstyle-security.xml`
- **Focus Areas**:
  - Visibility modifiers
  - Exception handling
  - Import restrictions
  - Parameter assignments

### Frontend Security (Angular/TypeScript)

#### 1. ESLint Security Plugin
- **Purpose**: Static analysis with security-focused rules
- **Configuration**: `frontend/.eslintrc-security.json`
- **Security Rules**:
  - Object injection detection
  - Unsafe regex patterns
  - eval() usage detection
  - Buffer security issues
  - CSRF protection

#### 2. NPM Audit CI
- **Purpose**: Dependency vulnerability scanning
- **Configuration**: `frontend/audit-ci.json`
- **Threshold**: Fails on high/critical vulnerabilities
- **Features**: Automated dependency security checks

#### 3. Retire.js
- **Purpose**: Detection of known vulnerable JavaScript libraries
- **Output**: `frontend/retire-report.json`
- **Scope**: Scans all frontend dependencies

#### 4. Lighthouse Security Audit
- **Purpose**: Performance and security analysis
- **Output**: `frontend/lighthouse-report.json`
- **Features**: Security best practices assessment

### Container Security

#### Trivy Scanner
- **Purpose**: Container image vulnerability scanning
- **Targets**: Both frontend and backend Docker images
- **Integration**: GitHub Security tab (SARIF format)

### Code Analysis

#### GitHub CodeQL
- **Purpose**: Semantic code analysis for security vulnerabilities
- **Languages**: Java and JavaScript/TypeScript
- **Features**: Deep static analysis with security focus

## 🚀 Running Security Tests

### Local Testing

#### Complete Security Test Suite
```bash
# Run all security tests
./scripts/security-test-all.sh
```

#### Backend Only
```bash
# Run backend security tests
./scripts/security-test-backend.sh
```

#### Frontend Only
```bash
# Run frontend security tests
./scripts/security-test-frontend.sh
```

### Individual Tool Testing

#### Backend
```bash
cd backend

# OWASP Dependency Check
mvn org.owasp:dependency-check-maven:check

# SpotBugs Security
mvn compile spotbugs:check

# PMD Security Rules
mvn pmd:check

# Checkstyle Security
mvn checkstyle:check
```

#### Frontend
```bash
cd frontend

# Install security dependencies
npm install

# ESLint Security
npm run lint:security

# NPM Audit
npm run audit-ci

# Retire.js
npm run retire

# Complete security scan
npm run security-scan
```

## 📊 Security Reports

### Generated Reports

| Tool | Location | Format | Description |
|------|----------|--------|-------------|
| OWASP Dependency Check | `backend/target/dependency-check-report.html` | HTML | Vulnerable dependencies |
| SpotBugs | `backend/target/spotbugsXml.xml` | XML | Security bugs |
| PMD | `backend/target/pmd.xml` | XML | Code quality/security |
| Checkstyle | `backend/target/checkstyle-result.xml` | XML | Coding standards |
| Retire.js | `frontend/retire-report.json` | JSON | Vulnerable libraries |
| Lighthouse | `frontend/lighthouse-report.json` | JSON | Performance/security |

### CI/CD Integration

The security testing pipeline runs:
- **On every push** to main/develop branches
- **On pull requests** to main branch
- **Daily at 2 AM UTC** (scheduled)

## 🔧 Configuration Management

### Suppression Files

#### OWASP Suppressions (`backend/owasp-suppressions.xml`)
- Suppress false positives
- Document accepted risks
- Version-specific suppressions

#### Security Rule Customization
- Adjust rule severity levels
- Add project-specific rules
- Configure thresholds

### Environment Variables

```bash
# Backend
TESSDATA_PREFIX=/usr/share/tesseract-ocr/5/tessdata
SPRING_PROFILES_ACTIVE=docker
UPLOAD_PATH=/app/uploads

# Security testing
FAIL_ON_SECURITY_ISSUES=true
SECURITY_THRESHOLD=high
```

## 📋 Security Checklist

### Pre-Deployment Security Verification

- [ ] All security tests pass locally
- [ ] No high/critical vulnerabilities in dependencies
- [ ] Security reports reviewed and approved
- [ ] Container images scanned for vulnerabilities
- [ ] CodeQL analysis completed successfully
- [ ] Security headers configured in production
- [ ] Input validation implemented
- [ ] Authentication/authorization tested
- [ ] File upload restrictions enforced
- [ ] Error handling doesn't leak sensitive information

### Regular Security Maintenance

- [ ] Monthly dependency updates
- [ ] Quarterly security tool updates
- [ ] Annual security configuration review
- [ ] Continuous monitoring of security advisories
- [ ] Regular penetration testing
- [ ] Security training for development team

## 🛠️ Troubleshooting

### Common Issues

#### 1. OWASP Dependency Check Failures
```bash
# Update NVD database
mvn org.owasp:dependency-check-maven:update-only

# Check specific dependency
mvn org.owasp:dependency-check-maven:check -Dformats=XML
```

#### 2. SpotBugs False Positives
- Review `backend/spotbugs-security-include.xml`
- Add exclusions for false positives
- Document security review decisions

#### 3. NPM Audit Issues
```bash
# Check audit details
npm audit

# Fix automatically (where possible)
npm audit fix

# Force fix (use with caution)
npm audit fix --force
```

#### 4. ESLint Security Violations
- Review specific rule violations
- Apply security fixes
- Add suppressions only for confirmed false positives

### Performance Optimization

#### Large Projects
- Configure parallel execution
- Use incremental analysis where available
- Cache security databases locally

#### CI/CD Optimization
- Use dependency caching
- Run security tests in parallel
- Store security reports as artifacts

## 📚 Additional Resources

### Security Standards
- [OWASP Top 10](https://owasp.org/www-project-top-ten/)
- [CWE/SANS Top 25](https://cwe.mitre.org/top25/)
- [Spring Security Best Practices](https://spring.io/guides/gs/securing-web/)
- [Angular Security Guide](https://angular.io/guide/security)

### Tool Documentation
- [OWASP Dependency Check](https://owasp.org/www-project-dependency-check/)
- [SpotBugs](https://spotbugs.github.io/)
- [FindSecBugs](https://find-sec-bugs.github.io/)
- [ESLint Security Plugin](https://github.com/nodesecurity/eslint-plugin-security)
- [Retire.js](https://retirejs.github.io/retire.js/)

## 🔒 Security Contact

For security-related questions or to report vulnerabilities:
- Create a security issue in the repository
- Follow responsible disclosure practices
- Include detailed reproduction steps
- Provide security impact assessment
