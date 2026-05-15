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
        <div
          className="mx-auto max-w-lg p-6 text-neutral-800"
          style={{ backgroundColor: '#faf8f5', color: '#2c2824' }}
        >
          <h1 className="text-lg font-semibold text-red-800">Load failed</h1>
          <p className="mt-2 text-sm text-neutral-600">{this.state.error.message}</p>
          <button
            type="button"
            className="mt-4 rounded-lg border border-neutral-300 bg-white px-4 py-2 text-sm text-neutral-900"
            onClick={() => this.setState({ error: null })}
          >
            Retry
          </button>
        </div>
      );
    }
    return this.props.children;
  }
}
