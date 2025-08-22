# Contributing to DocUp

We're excited that you're interested in contributing to DocUp! This document outlines the process for contributing to this project.

## 🚀 Getting Started

1. **Fork the repository** on GitLab
2. **Clone your fork** locally:
   ```bash
   git clone https://gitlab.com/your-username/docup.git
   cd docup
   ```
3. **Create a feature branch**:
   ```bash
   git checkout -b feature/your-feature-name
   ```

## 🔧 Development Setup

### Prerequisites
- Docker and Docker Compose
- Java 17+ (for backend development)
- Node.js 18+ (for frontend development)
- Git

### Local Development Environment

1. **Start the full application**:
   ```bash
   docker-compose up -d
   ```

2. **Or run components separately**:
   ```bash
   # Backend
   cd backend && ./mvnw spring-boot:run
   
   # Frontend
   cd frontend && npm install && npm start
   ```

## 📝 Coding Standards

### Backend (Java/Spring Boot)

- Follow [Google Java Style Guide](https://google.github.io/styleguide/javaguide.html)
- Use meaningful variable and method names
- Add JavaDoc comments for public methods
- Write unit tests for new functionality
- Use Spring Boot best practices:
  - Constructor injection over field injection
  - Use `@Service`, `@Repository`, `@Controller` annotations appropriately
  - Handle exceptions with `@ControllerAdvice`

#### Example Code Structure:
```java
@Service
public class FileProcessingService {
    
    private final OcrService ocrService;
    private final VirusScanService virusScanService;
    
    public FileProcessingService(OcrService ocrService, VirusScanService virusScanService) {
        this.ocrService = ocrService;
        this.virusScanService = virusScanService;
    }
    
    /**
     * Processes uploaded file with OCR and virus scanning.
     * @param file the uploaded file
     * @return processing result
     * @throws ProcessingException if processing fails
     */
    public ProcessingResult processFile(MultipartFile file) throws ProcessingException {
        // Implementation
    }
}
```

### Frontend (Angular/TypeScript)

- Follow [Angular Style Guide](https://angular.io/guide/styleguide)
- Use TypeScript strict mode
- Create reusable components
- Use reactive programming with RxJS
- Add unit tests for components and services

#### Example Component Structure:
```typescript
@Component({
  selector: 'app-upload',
  templateUrl: './upload.component.html',
  styleUrls: ['./upload.component.css']
})
export class UploadComponent implements OnInit {
  
  uploadProgress$ = new BehaviorSubject<number>(0);
  
  constructor(private uploadService: UploadService) {}
  
  ngOnInit(): void {
    // Initialization logic
  }
  
  onFileSelected(event: Event): void {
    // File selection logic
  }
}
```

## 🧪 Testing

### Running Tests

```bash
# Backend tests
cd backend && ./mvnw test

# Frontend tests
cd frontend && npm test

# Integration tests
./scripts/test-backend.sh
```

### Test Requirements

- **Unit Tests**: All new business logic must have unit tests
- **Integration Tests**: API endpoints should have integration tests
- **Coverage**: Aim for 80%+ code coverage for new code
- **Test Naming**: Use descriptive test names that explain what is being tested

#### Example Test:
```java
@Test
void shouldExtractTextFromValidImage() {
    // Given
    MultipartFile mockFile = createMockImageFile();
    
    // When
    String extractedText = ocrService.extractText(mockFile);
    
    // Then
    assertThat(extractedText).isNotEmpty();
    assertThat(extractedText).contains("expected text");
}
```

## 📋 Pull Request Process

### Before Submitting

1. **Update documentation** if you've made changes to APIs or features
2. **Run all tests** and ensure they pass
3. **Update the README** if needed
4. **Check code formatting** and linting
5. **Rebase your branch** on the latest main

### Pull Request Template

```markdown
## Description
Brief description of the changes made.

## Type of Change
- [ ] Bug fix (non-breaking change which fixes an issue)
- [ ] New feature (non-breaking change which adds functionality)
- [ ] Breaking change (fix or feature that would cause existing functionality to not work as expected)
- [ ] Documentation update

## Testing
- [ ] Unit tests pass
- [ ] Integration tests pass
- [ ] Manual testing completed

## Screenshots (if applicable)
Add screenshots to help explain your changes.

## Checklist
- [ ] My code follows the project's coding standards
- [ ] I have performed a self-review of my code
- [ ] I have commented my code, particularly in hard-to-understand areas
- [ ] I have made corresponding changes to the documentation
- [ ] My changes generate no new warnings
- [ ] I have added tests that prove my fix is effective or that my feature works
```

## 🐛 Bug Reports

### Before Reporting

1. **Search existing issues** to avoid duplicates
2. **Try the latest version** to see if the bug is already fixed
3. **Test with Docker** to rule out environment issues

### Bug Report Template

```markdown
**Bug Description**
A clear and concise description of what the bug is.

**Steps to Reproduce**
1. Go to '...'
2. Click on '....'
3. Scroll down to '....'
4. See error

**Expected Behavior**
What you expected to happen.

**Actual Behavior**
What actually happened.

**Environment**
- OS: [e.g. Ubuntu 22.04]
- Browser: [e.g. Chrome 120]
- Docker Version: [e.g. 24.0.0]
- Application Version: [e.g. v1.0.0]

**Additional Context**
Add any other context about the problem here.

**Logs**
```
Relevant log output here
```

## 💡 Feature Requests

### Feature Request Template

```markdown
**Feature Summary**
A clear and concise description of the feature you'd like to see.

**Problem Statement**
What problem does this feature solve?

**Proposed Solution**
Describe how you envision this feature working.

**Alternatives Considered**
Alternative approaches you've considered.

**Additional Context**
Any other context, mockups, or examples.
```

## 🔄 Development Workflow

### Git Workflow

We use a simplified Git flow:

```bash
# Create feature branch from main
git checkout main
git pull origin main
git checkout -b feature/your-feature

# Make changes and commit
git add .
git commit -m "feat: add amazing new feature"

# Push and create merge request
git push origin feature/your-feature
```

### Commit Message Format

Follow [Conventional Commits](https://www.conventionalcommits.org/):

```
<type>(<scope>): <description>

[optional body]

[optional footer]
```

**Types:**
- `feat`: A new feature
- `fix`: A bug fix
- `docs`: Documentation only changes
- `style`: Changes that do not affect the meaning of the code
- `refactor`: A code change that neither fixes a bug nor adds a feature
- `test`: Adding missing tests or correcting existing tests
- `chore`: Changes to the build process or auxiliary tools

**Examples:**
```
feat(upload): add drag and drop functionality
fix(ocr): handle large image processing errors
docs(readme): update installation instructions
```

## 🤝 Code Review Guidelines

### For Contributors

- **Keep changes focused**: One feature/fix per merge request
- **Write clear descriptions**: Explain what and why, not just what
- **Be responsive**: Address review comments promptly
- **Test thoroughly**: Ensure your changes work in different scenarios

### For Reviewers

- **Be constructive**: Provide helpful feedback, not just criticism
- **Ask questions**: If something is unclear, ask for clarification
- **Suggest improvements**: Share knowledge and best practices
- **Approve promptly**: Don't delay reviews unnecessarily

## 📚 Resources

- [Spring Boot Documentation](https://docs.spring.io/spring-boot/)
- [Angular Documentation](https://angular.io/docs)
- [Docker Documentation](https://docs.docker.com/)
- [GitLab CI/CD Documentation](https://docs.gitlab.com/ee/ci/)
- [Tesseract OCR Documentation](https://tesseract-ocr.github.io/)

## 🆘 Getting Help

- **Documentation**: Check the `/docs` folder and README
- **Issues**: Search existing issues or create a new one
- **Discussions**: Use GitLab discussions for questions
- **Email**: Contact the maintainers directly for urgent issues

Thank you for contributing to DocUp! 🚀
