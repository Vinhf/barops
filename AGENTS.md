# AGENTS.md

This repository contains a BarOps monorepo with a Spring Boot backend and a Next.js frontend. Use the project guidance here before making changes.

## Workspace map

- [README.md](README.md) — repo overview, setup, and required architectural rules.
- [docs/ERD.md](docs/ERD.md) — data model and domain conventions.
- [docs/sprint-1-plan.md](docs/sprint-1-plan.md) — sprint goals and expected scope.
- [backend/pom.xml](backend/pom.xml) — backend dependencies and Java/Spring Boot version.
- [frontend/package.json](frontend/package.json) — frontend scripts and dependencies.

## Core project rules

- Backend is Spring Boot 4.1.1 on Java 25.
- Frontend is Next.js + TypeScript.
- Shared catalog/domain entities that belong to a business extend `BaseTenantEntity`.
- Branch-scoped entities extend `BaseBranchEntity` and must validate access through `BranchAccessGuard.assertAccess(branchId)` before mutating or relying on a branch ID.
- New repositories should extend `TenantAwareRepository<T>` instead of writing manual business scoping logic.
- JWT claims include businessId, branchId, and role; preserve that model when adding auth or authorization changes.
- Use existing naming and package conventions under `com.barops.backend.*`, especially the `core` package structure.

## Build and verification

- Backend tests: from the `backend` folder, run `./mvnw test`.
- Backend app startup: from the `backend` folder, run `./mvnw spring-boot:run`.
- Frontend install/dev: from the `frontend` folder, run `npm install` and `npm run dev`.
- Prefer the smallest relevant verification command for the area being changed.

## Editing expectations

- Prefer surgical, targeted edits over broad refactors.
- Preserve existing package boundaries and domain ownership.
- Add or update tests for backend behavior changes when practical.
- Keep documentation links and architecture intent intact; do not re-implement patterns that already exist in the codebase.

## Global design system rules for UI work

Any time you generate, edit, refactor, or style HTML, CSS, React, Vue, Tailwind CSS, or UI components, you must follow the design guidelines in the design system repository located here:

C:\code\promtUI\awesome-design-md-main\awesome-design-md-main

Before writing UI code:

1. Read the Markdown documentation in that path, especially files in the `design-md` folder, to confirm design constraints.
2. Follow the specified color palette, typography scale, spacing system, layout rules, and component patterns exactly.
3. Keep the UI visually consistent, accessible, and aligned with the defined theme variables and patterns.
4. Do not invent styles that conflict with those design rules; prefer the established system values.

When making UI changes, use the project’s existing structure and keep the implementation consistent with the repo’s broader architecture.

## Recommended workflow for agents

- Start with the repo docs and the most relevant package before editing.
- Keep changes scoped to the requested task.
- Validate with the smallest command that checks the changed behavior.
- If the task is UI-related, check the design system docs before implementation and again when finalizing the styling.
