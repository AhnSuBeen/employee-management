# Development Workflow

This repo uses git with a protected `main` branch. `main` cannot be committed to directly —
every change lands through a `feature/<slug>` branch and a pull request. "Reviewing a change"
means reviewing the PR diff on GitHub, not just the local files. These rules are mandatory for
every non-trivial change (new features, schema changes, config changes). Read-only exploration
(reading files, searching, running the app to observe behavior) is always allowed without a plan
or a branch.

## The pipeline

1. **Spec** (`docs/specs/<feature-slug>.md`) — the *what and why*. Problem statement,
   requirements, acceptance criteria, explicit out-of-scope items. Written and agreed with the
   user before any plan is drafted.
2. **Plan** (`docs/plans/<feature-slug>.md`) — the *how*. Step-by-step implementation approach,
   files/modules to touch, sequencing, risks or open questions. Written after the spec is settled,
   before any code is touched.
3. **Approval gate** — present the plan to the user and get an explicit go-ahead ("approved",
   "looks good", "proceed", etc.). Use plan mode (`EnterPlanMode` / `ExitPlanMode`) for this.
   Silence or a vague reply is not approval — ask for a clear yes.
4. **Branch** — create `feature/<feature-slug>` from the latest `main` before writing any code.
   Never commit to `main` directly (branch protection rejects it anyway).
5. **Implement** — write code strictly within the boundaries of the approved spec and plan, on
   the feature branch. Split commits into meaningful units (e.g. one commit per logical step —
   schema, then service, then controller, then UI — not one giant commit and not one commit per
   file save).
6. **Verify** — run/exercise the app locally and check the result against the spec's acceptance
   criteria before opening a PR.
7. **Pull request** — push the feature branch and open a PR into `main` describing what changed
   and why, referencing the spec/plan. Merge only after the PR is reviewed (self-review is fine
   for solo work, but it must happen as a PR review, not a silent merge).

## Hard rules

- **Plan-first.** No source file is written or edited until a plan document exists in
  `docs/plans/` and the user has explicitly approved it in conversation.
- **No code before approval.** This applies even to changes that look small or "obvious" once a
  spec/plan is in flight. If the user asks for a trivial one-off unrelated to any spec, treat it
  as its own tiny spec+plan cycle rather than skipping the gate.
- **No direct commits to `main`.** All work happens on a `feature/<slug>` branch and reaches
  `main` only via a merged PR. `main` is protected on GitHub (PR required, force-push and branch
  deletion disabled, enforced even for repo admins).
- **Implement only within the approved spec.** If something outside the spec's scope is needed
  (missing requirement, new edge case, unrelated bug spotted along the way), stop, surface it to
  the user, and update the spec/plan (with re-approval) before continuing. Don't silently expand
  scope ("while I'm here...").
- **Ask, don't guess.** When a requirement, acceptance criterion, data shape, or design choice is
  ambiguous or missing from the spec, ask the user instead of assuming. Record the answer back
  into the spec so it isn't re-litigated later.

## File and branch conventions

- `docs/specs/<feature-slug>.md` — one file per feature/change, see `docs/specs/README.md` for
  the template.
- `docs/plans/<feature-slug>.md` — one file per feature/change, matching slug to its spec, see
  `docs/plans/README.md` for the template.
- Slugs are kebab-case and short, e.g. `employee-crud-api`, `auth-jwt`.
- Branch name matches the slug: `feature/<feature-slug>` (e.g. `feature/employee-crud-api`).
- Commit messages are imperative and scoped to what that commit actually changes (e.g. "Add
  Employee entity and repository", not "wip" or "more changes").
