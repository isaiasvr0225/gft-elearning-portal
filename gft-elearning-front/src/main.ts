import { bootstrapApplication } from '@angular/platform-browser';
import { provideRouter, withComponentInputBinding } from '@angular/router';
import { provideHttpClient } from '@angular/common/http';

import { routes } from './app/app.routes';
import { AppShell } from './app/app.shell';

import { appConfig } from './app/app.config';

bootstrapApplication(AppShell, appConfig);
