#!/bin/bash
# ════════════════════════════════════════════════════════════════════════
# Emras — One-time Git + GitHub setup
# Run this ONCE after extracting the project, from the emras/ root folder.
# ════════════════════════════════════════════════════════════════════════

set -e

REPO_NAME="emras"
GITHUB_USER="Rifat-Tipu"
REMOTE_URL="https://github.com/${GITHUB_USER}/${REPO_NAME}.git"

echo "🚀 Setting up Emras Git repository..."
echo ""
echo "⚠️  BEFORE running this script, create an EMPTY repo on GitHub:"
echo "    1. Go to https://github.com/new"
echo "    2. Repository name: ${REPO_NAME}"
echo "    3. Keep it EMPTY — do NOT add README/.gitignore/license"
echo "    4. Click 'Create repository'"
echo ""
read -p "Press Enter once you've created the empty repo on GitHub..."

# ── Init repo ──────────────────────────────────────────────────────────
if [ ! -d ".git" ]; then
  git init
  echo "✅ Git initialized"
fi

git branch -M main

# ── Connect remote ───────────────────────────────────────────────────────
if git remote get-url origin >/dev/null 2>&1; then
  echo "ℹ️  Remote 'origin' already set, skipping"
else
  git remote add origin "$REMOTE_URL"
  echo "✅ Remote added: $REMOTE_URL"
fi

# ── Initial commit on main ───────────────────────────────────────────────
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

git push -u origin main
echo "✅ Pushed to main"

# ── Create dev branch ────────────────────────────────────────────────────
git checkout -b dev
git push -u origin dev
echo "✅ Created and pushed branch: dev"

# ── Create first feature branch for today's work ────────────────────────
git checkout -b feature/1.1-project-setup
git push -u origin feature/1.1-project-setup
echo "✅ Created and pushed branch: feature/1.1-project-setup"

echo ""
echo "🎉 Done! Your repo is live at:"
echo "   https://github.com/${GITHUB_USER}/${REPO_NAME}"
echo ""
echo "✅ Branch structure:"
echo "   main         ← production releases only"
echo "   dev          ← integration / staging (merge features here first)"
echo "   feature/*    ← one branch per feature, cut from dev"
echo ""
echo "📌 You are currently on: feature/1.1-project-setup"
echo "   This already contains today's work and is pushed."
echo ""
echo "📌 Daily workflow from tomorrow onward — see push-feature.sh"
