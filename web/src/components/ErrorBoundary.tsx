import { Component, type ErrorInfo, type ReactNode } from 'react';

interface Props {
  children: ReactNode;
}

interface State {
  error: Error | null;
}

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
        <div className="app-shell app-page">
          <h1 className="type-section-title" style={{ color: 'var(--color-danger)' }}>
            화면을 불러오지 못했습니다
          </h1>
          <p className="type-body mt-3" style={{ color: 'var(--color-text-secondary)' }}>
            {this.state.error.message}
          </p>
          <button
            type="button"
            className="app-btn app-btn-primary mt-6"
            onClick={() => this.setState({ error: null })}
          >
            다시 시도
          </button>
        </div>
      );
    }
    return this.props.children;
  }
}
