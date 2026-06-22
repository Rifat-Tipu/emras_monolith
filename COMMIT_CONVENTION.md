# Commit Message Convention

Emras follows [Conventional Commits](https://www.conventionalcommits.org/) so the
Git history reads like a changelog — exactly how organizations track progress.

## Format

```
<type>(<scope>): <short description>

[optional longer body]
```

## Types

| Type       | When to use it                                          |
|------------|----------------------------------------------------------|
| `feat`     | A new feature (e.g. new API endpoint, new UI page)       |
| `fix`      | A bug fix                                                 |
| `chore`    | Tooling, config, dependency bumps, project scaffolding    |
| `refactor` | Code change that isn't a new feature or a fix             |
| `docs`     | Documentation only                                         |
| `test`     | Adding or fixing tests                                     |
| `style`    | Formatting, whitespace — no logic change                  |
| `perf`     | Performance improvement                                    |

## Scopes (Emras domains)

`auth` · `user` · `product` · `category` · `cart` · `order` · `payment` · `ai` ·
`db` · `security` · `cache` · `config` · `frontend` · `deploy`

## Examples

```
feat(auth): add JWT login and refresh token endpoints
feat(product): implement product listing with pagination and filters
fix(cart): correct stock validation on quantity update
chore(db): add Flyway migration for orders and order_items tables
refactor(payment): extract promo code validation into separate service
docs(readme): update setup instructions for Phase 1.2
feat(frontend): build product card component with hover animation
```

## Daily Workflow Example

```bash
# ... after finishing a feature, from the emras/ root ...

bash push-feature.sh "1.5-auth" "feat(auth): add JWT login, register, and refresh token APIs"
```

This single command creates the feature branch (if needed), commits, pushes it,
and merges it into `dev` — automatically. See `push-feature.sh` for details.

## When to merge into `main`

Only merge `dev` → `main` when an entire **phase** (per the Implementation Plan)
is complete and tested end-to-end — not after every single feature.

```bash
git checkout main
git merge dev
git push origin main
git tag -a v0.1.0-phase1 -m "Phase 1: Project Foundation complete"
git push origin v0.1.0-phase1
```

Tagging phase completions (`v0.1.0-phase1`, `v0.2.0-phase2`, ...) gives you
clear, professional milestones in your GitHub repo.
