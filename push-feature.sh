#!/bin/bash
# ════════════════════════════════════════════════════════════════════════
# Emras — Daily feature push helper
#
# USAGE:
#   bash push-feature.sh "1.2-database-schema" "feat(db): add Flyway migrations for core tables"
#
#   arg 1 → feature slug (used in branch name: feature/<slug>)
#   arg 2 → commit message (conventional commits style recommended)
#
# WHAT IT DOES:
#   1. Switches to dev and pulls latest
#   2. Creates feature/<slug> branch (or reuses it if it already exists)
#   3. Stages + commits all current changes
#   4. Pushes the feature branch to GitHub
#   5. Merges feature branch into dev and pushes dev
#
# Run this at the end of each working session / each completed feature.
# ════════════════════════════════════════════════════════════════════════

set -e

SLUG="$1"
MESSAGE="$2"

if [ -z "$SLUG" ] || [ -z "$MESSAGE" ]; then
  echo "❌ Usage: bash push-feature.sh \"<feature-slug>\" \"<commit message>\""
  echo "   Example: bash push-feature.sh \"1.2-database-schema\" \"feat(db): add Flyway migrations\""
  exit 1
fi

BRANCH="feature/${SLUG}"

echo "🔄 Switching to dev and pulling latest..."
git checkout dev
git pull origin dev

echo "🌿 Creating/switching to ${BRANCH}..."
if git show-ref --verify --quiet "refs/heads/${BRANCH}"; then
  git checkout "$BRANCH"
  git merge dev --no-edit
else
  git checkout -b "$BRANCH"
fi

echo "📦 Staging and committing changes..."
git add .
git commit -m "$MESSAGE" || echo "ℹ️  Nothing new to commit, continuing..."

echo "⬆️  Pushing ${BRANCH} to GitHub..."
git push -u origin "$BRANCH"

echo "🔀 Merging ${BRANCH} into dev..."
git checkout dev
git merge "$BRANCH" --no-edit
git push origin dev

echo ""
echo "✅ Done! Today's work is on:"
echo "   - origin/${BRANCH}  (feature history, preserved)"
echo "   - origin/dev        (integrated)"
echo ""
echo "📌 When a full PHASE is complete and tested, merge dev → main:"
echo "   git checkout main && git merge dev && git push origin main"
