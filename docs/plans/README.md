# Plans

A plan captures **how** to implement an already-approved spec. One file per feature/change,
named `<feature-slug>.md` matching the spec of the same slug in `docs/specs/`.

A plan is not written until its spec is settled, and no code is written until the plan itself is
explicitly approved by the user. See `.claude/rules/development-workflow.md`.

This is a git-free local project — plans have no branch/commit section, just an implementation
order.

## Template

```markdown
# <Feature name> — implementation plan

Spec: docs/specs/<feature-slug>.md

## Approach
High-level strategy and any alternatives considered.

## Steps
1. Step one — files/modules touched
2. Step two — files/modules touched
3. ...

## Risks / open questions
Anything uncertain that should be flagged before or during implementation.
```
