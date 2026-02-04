const { GradleLibraryProject } = require('@gplassard/projen-extensions');

const project = new GradleLibraryProject({
   name: 'smithy-extensions',
});
project.synth();