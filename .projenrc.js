const { GradleLibraryProject, WorkflowActionsX } = require('@gplassard/projen-extensions');
const { github } = require('projen');

const project = new GradleLibraryProject({
   name: 'smithy-extensions',
});

const releaseWorkflow = project.github.addWorkflow('release-open-api');
releaseWorkflow.on({
  push: {
    tags: ['open-api-*'],
  },
});

// Build and publish to GitHub Packages
releaseWorkflow.addJob('build-github', {
  runsOn: ['ubuntu-latest'],
  env: {
    CODE_ARTIFACT_URL: '${{ secrets.CODE_ARTIFACT_URL }}',
  },
  permissions: {
    contents: github.workflows.JobPermission.READ,
    packages: github.workflows.JobPermission.WRITE,
    idToken: github.workflows.JobPermission.WRITE,
  },
  steps: [
    WorkflowActionsX.checkout(),
    WorkflowActionsX.setupJdk({}),
    {
      name: 'Build',
      run: './gradlew build',
    },
    {
      name: 'Publish package',
      run: './gradlew :open-api:publishAllPublicationsToGithubPackagesRepository',
      env: {
        GITHUB_TOKEN: '${{ secrets.GITHUB_TOKEN }}',
      },
    },
  ],
});

// Build and publish to AWS CodeArtifact
releaseWorkflow.addJob('build-code-artifact', {
  runsOn: ['ubuntu-latest'],
  env: {
    CODE_ARTIFACT_URL: '${{ secrets.CODE_ARTIFACT_URL }}',
  },
  permissions: {
    contents: github.workflows.JobPermission.READ,
    packages: github.workflows.JobPermission.WRITE,
    idToken: github.workflows.JobPermission.WRITE,
  },
  steps: [
    WorkflowActionsX.checkout(),
    WorkflowActionsX.setupJdk({}),
    {
      name: 'Build',
      run: './gradlew build',
    },
    WorkflowActionsX.configureAwsCredentials('${{ secrets.CODE_ARTIFACT_WRITE_ROLE }}'),
    WorkflowActionsX.generateCodeArtifactToken(),
    {
      name: 'Publish package',
      run: './gradlew :open-api:publishAllPublicationsToCodeArtifactRepository',
      env: {
        CODEARTIFACT_AUTH_TOKEN: '${{ steps.code-artifact-token.outputs.token }}',
      },
    },
  ],
});

project.synth();
