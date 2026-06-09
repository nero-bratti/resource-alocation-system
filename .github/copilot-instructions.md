# Copilot / AI tooling hints

This repository includes an `ai-skills/` manifest to declare repository-local AI skills.

- Use `python tools/skill_runner.py --list` to enumerate skills.
- Use `python tools/skill_runner.py run <skill-id> --dry-run` to simulate execution.

When adding new skills, update `ai-skills/manifest.yaml` and provide a safe, documented sample skill file under `ai-skills/`.
