## Description

<!-- Briefly describe the change. -->

## Checklist

- [ ] The target of this pull request is the `develop` branch, not `main` (unless this is a release)
- [ ] Unit tests have been added for this change, if applicable
- [ ] End-to-end tests have been added for this change, if applicable
- [ ] The change is documented on the website, if it's a user-facing change (e.g., new syntax, a new stream operator, a new accelerator annotation)
- [ ] TODO comments and uses of `???` have been removed
- [ ] The pre-commit hook passes (scalafmt, unit tests, end-to-end tests, etc.)
- All tests pass, including those that are not run in the pre-commit hook (because they rely on `vsim`, are too slow, etc.):
    - [ ] Unit tests
    - [ ] End-to-end tests
