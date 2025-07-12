import React from 'react';
import { cn } from '@/lib/utils';

export interface InputProps extends React.InputHTMLAttributes<HTMLInputElement> {
  label?: string;
  iconLeft?: React.ReactNode;
  iconRight?: React.ReactNode;
}

export const Input = React.forwardRef<HTMLInputElement, InputProps>(
  ({ className, label, iconLeft, iconRight, ...props }, ref) => {
    return (
      <div className="w-full">
        {label && (
          <label className="mb-1.5 block text-xs font-medium text-gray-400">
            {label}
          </label>
        )}
        <div className="relative">
          {iconLeft && (
            <div className="absolute left-3 top-1/2 -translate-y-1/2 text-gray-500 pointer-events-none">
              {iconLeft}
            </div>
          )}
          <input
            ref={ref}
            className={cn(
              'w-full rounded-lg border border-white/10 bg-white/5 py-2.5 text-sm text-white placeholder-gray-600 outline-none transition-all focus:border-violet-500/50 focus:ring-1 focus:ring-violet-500/25',
              iconLeft ? 'pl-9' : 'px-4',
              iconRight ? 'pr-10' : 'pr-4',
              className
            )}
            {...props}
          />
          {iconRight && (
            <div className="absolute right-3 top-1/2 -translate-y-1/2 text-gray-500">
              {iconRight}
            </div>
          )}
        </div>
      </div>
    );
  }
);
Input.displayName = 'Input';
