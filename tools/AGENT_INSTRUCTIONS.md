Purpose
- Utility scripts and repo-local AI skills used by agents and maintainers.

Key files
- `tools/skill_runner.py` — enumerates and runs repo-local AI skills (dry-run mode by default).
- `ai-skills/` — local skills manifest and samples.

Usage
- List skills: `python tools/skill_runner.py --list`
- Dry-run a skill: `python tools/skill_runner.py run <skill-id> --dry-run`

Notes for agents
- Skill files are intentionally dry-run; set `auto_execute: true` in `ai-skills/manifest.yaml` only after safety review.
- When adding new skills, include documentation and safe default behavior.
