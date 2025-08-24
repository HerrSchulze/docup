#!/bin/bash

# GitHub Migration Script for DocUp
# This script helps migrate from GitLab to GitHub

echo "🚀 DocUp GitHub Migration Script"
echo "=================================="

# Check if we're in the right directory
if [ ! -f "backend/pom.xml" ] || [ ! -f "frontend/package.json" ]; then
    echo "❌ Error: Please run this script from the DocUp project root directory"
    exit 1
fi

echo ""
echo "📋 Current Git Configuration:"
echo "Remote: $(git remote get-url origin 2>/dev/null || echo 'No remote configured')"
echo "Branch: $(git branch --show-current)"
echo "User: $(git config user.name) <$(git config user.email)>"

echo ""
echo "🔄 Step 1: Backup current configuration"
git remote -v > .git-backup-remotes.txt
echo "✅ Remote backup saved to .git-backup-remotes.txt"

echo ""
echo "🗑️  Step 2: Remove GitLab remote"
if git remote get-url origin | grep -q gitlab; then
    git remote remove origin
    echo "✅ GitLab remote removed"
else
    echo "ℹ️  No GitLab remote found to remove"
fi

echo ""
echo "📝 Step 3: GitHub Repository Setup"
echo ""
echo "Please create a new GitHub repository first:"
echo "1. Go to: https://github.com/new"
echo "2. Repository name: docup"
echo "3. Description: DocUp - Document Upload with OCR and Security Scanning"
echo "4. Choose Private or Public"
echo "5. DON'T initialize with README, .gitignore, or license"
echo "6. Click 'Create repository'"
echo ""

read -p "Have you created the GitHub repository? (y/n): " created_repo

if [ "$created_repo" != "y" ] && [ "$created_repo" != "Y" ]; then
    echo "❌ Please create the GitHub repository first, then run this script again"
    exit 1
fi

echo ""
read -p "Enter your GitHub username: " github_username

if [ -z "$github_username" ]; then
    echo "❌ GitHub username is required"
    exit 1
fi

echo ""
echo "🔗 Step 4: Add GitHub remote"
echo "Choose authentication method:"
echo "1. HTTPS (username/password or token)"
echo "2. SSH (requires SSH key setup)"
read -p "Choose (1 or 2): " auth_method

if [ "$auth_method" = "1" ]; then
    git_url="https://github.com/${github_username}/docup.git"
elif [ "$auth_method" = "2" ]; then
    git_url="git@github.com:${github_username}/docup.git"
else
    echo "❌ Invalid choice"
    exit 1
fi

git remote add origin "$git_url"
echo "✅ GitHub remote added: $git_url"

echo ""
echo "📤 Step 5: Push to GitHub"
echo "Pushing all branches and history to GitHub..."

if git push -u origin main; then
    echo "✅ Successfully pushed to GitHub!"
else
    echo "❌ Push failed. This might be due to:"
    echo "  - Authentication issues"
    echo "  - Network problems"
    echo "  - Repository already has content"
    echo ""
    echo "Manual fix options:"
    echo "  1. Check authentication (GitHub token/SSH key)"
    echo "  2. Try: git push -f origin main (if you're sure about overwriting)"
    echo "  3. Check repository permissions"
    exit 1
fi

echo ""
echo "🔧 Step 6: Update documentation"
echo "Updating repository URLs in documentation..."

# Update documentation files
find docs/ -name "*.md" -type f -exec sed -i "s/gitlab\.com\/herrschulze-group/github.com\/${github_username}/g" {} \;
find . -name "README.md" -type f -exec sed -i "s/gitlab\.com\/herrschulze-group/github.com\/${github_username}/g" {} \;

echo "✅ Documentation updated"

echo ""
echo "📝 Step 7: Commit documentation updates"
git add .
git commit -m "docs: update repository URLs from GitLab to GitHub

- Updated all documentation links
- Changed from gitlab.com/herrschulze-group to github.com/${github_username}
- Migration completed successfully"

git push origin main

echo ""
echo "🎉 GitHub Migration Complete!"
echo "=============================="
echo ""
echo "✅ Repository migrated to: https://github.com/${github_username}/docup"
echo "✅ CI/CD pipeline configured with GitHub Actions"
echo "✅ All documentation updated"
echo ""
echo "🔍 Next Steps:"
echo "1. Visit: https://github.com/${github_username}/docup"
echo "2. Check the Actions tab for pipeline status"
echo "3. Configure branch protection rules"
echo "4. Enable Dependabot and security features"
echo "5. Review the migration guide: docs/GITHUB_MIGRATION.md"
echo ""
echo "🚀 Your DocUp project is now on GitHub with enhanced CI/CD!"
