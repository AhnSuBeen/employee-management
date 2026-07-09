# Specs

A spec captures **what** to build and **why**, before any planning or code. One file per
feature/change, named `<feature-slug>.md` (kebab-case, matching the plan of the same slug in
`docs/plans/`).

See `.claude/rules/development-workflow.md` for how specs fit into the overall workflow.

## Template

```markdown
# <Feature name>

## Problem / motivation
Why is this needed? What's the current pain or gap?

## Requirements
- Functional requirement 1
- Functional requirement 2

## Acceptance criteria
- [ ] Criterion 1
- [ ] Criterion 2

## Out of scope
- Explicitly excluded items, to prevent scope creep during implementation.

## Open questions
- Anything ambiguous that needs an answer from the user before planning starts.
```
