const { spawn } = require('node:child_process');
const path = require('node:path');

const root = path.resolve(__dirname, '..');
const wrapper = path.join(
  root,
  process.platform === 'win32' ? 'gradlew.bat' : 'gradlew',
);
const gradle = spawn(wrapper, process.argv.slice(2), {
  cwd: root,
  stdio: 'inherit',
  shell: process.platform === 'win32',
});

gradle.on('error', (error) => {
  console.error(`Unable to start the Gradle wrapper: ${error.message}`);
  process.exitCode = 1;
});

gradle.on('exit', (code, signal) => {
  if (signal) {
    console.error(`Gradle exited because of signal ${signal}`);
    process.exitCode = 1;
    return;
  }
  process.exitCode = code ?? 1;
});
