# OWASP Dependency Check API Key Setup

## Issue
OWASP Dependency Check requires access to the National Vulnerability Database (NVD) API. Starting in 2023, the NVD implemented rate limiting that often causes failures in CI/CD environments.

## Error Message
```
UpdateException: Error updating the NVD Data; the NVD returned a 403 or 404 error
Consider using an NVD API Key
```

## Solution: Get a Free NVD API Key

### Step 1: Request API Key
1. Visit: https://nvd.nist.gov/developers/request-an-api-key
2. Fill out the form with:
   - **Email**: Your email address
   - **Organization**: Your company/organization name
   - **Use Case**: "OWASP Dependency Check for security scanning"
3. Submit the request
4. You'll receive an API key via email (usually within minutes)

### Step 2: Add API Key to GitHub Secrets
1. Go to your GitHub repository: https://github.com/HerrSchulze/docup
2. Navigate to: **Settings** → **Secrets and variables** → **Actions**
3. Click **New repository secret**
4. Set:
   - **Name**: `NVD_API_KEY`
   - **Secret**: (paste your API key here)
5. Click **Add secret**

### Step 3: Verify Setup
The GitHub Actions workflow is already configured to use the API key automatically once you add it as a secret.

## Alternative: Skip OWASP for Now

If you don't want to set up the API key immediately, the security tests will continue with other tools:

1. **SpotBugs with FindSecBugs** - Static code analysis
2. **PMD Security Rules** - Code quality and security
3. **Checkstyle Security** - Coding standards

The OWASP check will show a warning but won't fail the entire pipeline.

## Local Development

### With API Key
```bash
export NVD_API_KEY="your-api-key-here"
export MAVEN_OPTS="-Dnvd.api.key=$NVD_API_KEY"
./scripts/security-test-backend.sh
```

### Without API Key (will show warnings)
```bash
./scripts/security-test-backend.sh
```

## Configuration Applied

### 1. Enhanced pom.xml Configuration
- Added timeout settings for better reliability
- Configured fallback behavior
- Set appropriate cache duration

### 2. Updated Security Script
- Graceful handling of OWASP failures
- Continues with other security tests
- Clear error messages and guidance

### 3. GitHub Actions Integration
- Automatic API key usage from secrets
- Environment variable configuration
- Artifact collection even on partial failures

## Benefits of Using API Key

1. **Reliable CI/CD**: No more random 403/404 errors
2. **Up-to-date Data**: Access to latest vulnerability information
3. **Better Performance**: Higher rate limits for faster scans
4. **Professional Use**: Recommended for production applications

## Next Steps

1. **Immediate**: The pipeline will continue with other security tests
2. **Recommended**: Get the free API key for complete security coverage
3. **Optional**: Set up additional security monitoring tools

The security pipeline will now be more resilient and provide useful results even without the API key!
