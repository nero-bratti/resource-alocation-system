#!/usr/bin/env python3
"""
Simple local skill runner (dry-run mode by default).
Reads `ai-skills/manifest.yaml` and lists skills; can execute simple steps from `ai-skills/sample_skill.yml`.

Usage:
  python tools/skill_runner.py --list
  python tools/skill_runner.py run check-permission-endpoint --dry-run
"""
import argparse
import os
import sys
import yaml

ROOT = os.path.dirname(os.path.dirname(__file__))
MANIFEST = os.path.join(ROOT, 'ai-skills', 'manifest.yaml')
SAMPLE = os.path.join(ROOT, 'ai-skills', 'sample_skill.yml')


def load_yaml(path):
    with open(path, 'r', encoding='utf-8') as f:
        return yaml.safe_load(f)


def list_skills():
    manifest = load_yaml(MANIFEST)
    for s in manifest.get('skills', []):
        print(f"- {s['id']}: {s.get('title')} -- triggers={s.get('triggers')}")


def run_sample(skill_id, dry_run=True):
    skill = load_yaml(SAMPLE)
    if skill.get('id') != skill_id:
        print(f"Sample loader: unknown skill {skill_id}")
        return 2
    steps = skill.get('steps', [])
    for step in steps:
        name = step.get('name')
        cmd = step.get('run')
        print(f"[step] {name}: {cmd}")
        if dry_run:
            print("  (dry-run) not executing")
        else:
            rc = os.system(cmd)
            if rc != 0:
                print(f"  step failed with rc={rc}")
                return rc
    return 0


if __name__ == '__main__':
    parser = argparse.ArgumentParser()
    parser.add_argument('--list', action='store_true', help='List available skills')
    sub = parser.add_subparsers(dest='cmd')
    runp = sub.add_parser('run')
    runp.add_argument('skill_id')
    runp.add_argument('--dry-run', action='store_true', default=True)

    args = parser.parse_args()
    if args.list:
        list_skills()
        sys.exit(0)
    if args.cmd == 'run':
        rc = run_sample(args.skill_id, dry_run=args.dry_run)
        sys.exit(rc)
    parser.print_help()
