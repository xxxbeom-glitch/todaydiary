# -*- coding: utf-8 -*-
from pathlib import Path

ROOT = Path(__file__).resolve().parents[1] / "src"
TAG_O = "[[[TAG_OPEN_DIV]]]"
TAG_C = "[[[TAG_CLOSE_DIV]]]"

def tags(s: str) -> str:
    open_tag = "<" + "div"
    close_tag = "</" + "motion>"
    close_tag = "</" + "div" + ">"
    return s.replace(TAG_O, open_tag).replace(TAG_C, close_tag)

ERROR_BOUNDARY = tags("""
import { Component, type ErrorInfo, type ReactNode } from 'react';

interface Props { children: ReactNode; }
interface State { error: Error | null; }

export class ErrorBoundary extends Component<Props, State> {
  state: State = { error: null };

  static getDerivedStateFromError(error: Error): State {
    return { error };
  }

  componentDidCatch(error: Error, info: ErrorInfo): void {
    console.error('[ErrorBoundary]', error, info.componentStack);
  }

  render() {
    if (this.state.error) {
      return (
        TAG_O className="mx-auto max-w-lg p-6 text-neutral-800" style={{ background: '#faf8f5' }}TAG_C
          TAG_O
          TAG_C
            TAG_O className="text-lg font-semibold text-red-800"TAG_C
              \ud654\uba74\uc744 \ubd88\ub7ec\uc624\uc9c0 \ubabb\ud588\uc2b5\ub2c8\ub2e4
            TAG_C
            TAG_O className="mt-2 text-sm text-neutral-600"TAG_C
              {this.state.error.message}
            TAG_C
            TAG_O
              type="button"
              className="mt-4 rounded-lg border border-neutral-300 bg-white px-4 py-2 text-sm text-neutral-900"
              onClick={() => this.setState({ error: null })}
            TAG_C
              \ub2e4\uc2dc \uc2dc\ub3c4
            TAG_C
          TAG_C
        TAG_C
      );
    }
    return this.props.children;
  }
}
""")

# Simpler approach - write files directly with open/close built in script
def w(rel: str, lines: list[str]) -> None:
    (ROOT / rel).write_text("\n".join(lines) + "\n", encoding="utf-8")

open_tag = "<" + "div"
close_tag = "</" + "motion>"
# FIX close tag
close_tag = "</" + "div" + ">"
