#!/bin/bash
# Run once after cloning/creating the repo to set up branch structure

set -e

echo "🚀 Setting up Emras Git repository..."

if [ ! -d ".git" ]; then
  git init
  echo "✅ Git initialized"
fi

git add .
git commit -m "chore: initial project scaffold

- Spring Boot 4.1.0 monolith foundation (Java 21, Spring Framework 7)
- Clean domain-driven package structure
- Shared: ApiResponse, BaseEntity, GlobalExceptionHandler
- Constants: ApiConstants, SuccessMessages, ErrorMessages
- Config: CORS, Cache (Caffeine), JPA Auditing, MDC filter, Security baseline
- Multi-profile properties: local / dev / prod
- Logback structured logging with MDC traceId
- Docker Compose: PostgreSQL 18 + MailHog
- .gitignore and .env.example"

git checkout -b dev
echo "✅ Created branch: dev"

git checkout main
echo ""
echo "✅ Branch structure ready:"
echo "   main         ← production releases"
echo "   dev          ← integration / staging"
echo "   feature/*    ← cut from dev, merge back to dev"
echo ""
echo "📌 Workflow:"
echo "   git checkout dev"
echo "   git checkout -b feature/1.5-auth"
echo "   # ... code ..."
echo "   git checkout dev && git merge feature/1.5-auth"
echo "   # When stable → merge dev into main"
