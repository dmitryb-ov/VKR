function openEditorFunc() {
    import {series} from 'async';
    const {exec} = require('child_process');

    series([
        () => exec('npm --prefix D:\\BPMN Module\\bpmn-js run all'),
    ]);
}