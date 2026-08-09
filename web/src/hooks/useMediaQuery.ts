import { useEffect, useState } from 'react';

/** Client-only media query match. SSR/first paint defaults to `false`. */
export function useMediaQuery(query: string): boolean {
  const [matches, setMatches] = useState(false);

  useEffect(() => {
    const media = window.matchMedia(query);
    const onChange = () => setMatches(media.matches);
    onChange();
    media.addEventListener('change', onChange);
    return () => media.removeEventListener('change', onChange);
  }, [query]);

  return matches;
}

export function useIsDesktopLayout(): boolean {
  return useMediaQuery('(min-width: 1024px)');
}
