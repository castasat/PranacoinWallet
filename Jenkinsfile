#!/usr/bin/env groovy

/* Defining Shared Libraries */
library retriever: modernSCM([$class: 'GitSCMSource', credentialsId: 'SA_CI', remote: 'https://stash.sigma.sbrf.ru/scm/sudevops/jenkins.git']),
    identifier: 'ru.sbrf.sudevops@master', changelog: false

android (
    BUILD_DIRECTORY: 'app/build/outputs/apk',
    // DESC: При отключении сквозного инкремента, необходимо удалить INCREMENT_GIT_URL и INCREMENT_FILE_NAME
    //INCREMENT_GIT_URL: 'https://stash.sigma.sbrf.ru/scm/sbapps/pranacoinwallet-android.git',
    //INCREMENT_FILE_NAME: 'android-version-code.txt',

    // Unit tests
    JAVA_HOME: '/Library/Java/JavaVirtualMachines/adoptopenjdk-11.jdk/Contents/Home',
    UNIT_COMMAND: './gradlew clean test',
    // Develop
    BUILD_COMMAND_DEV: './gradlew clean assembleDebug --no-daemon',
    // Production
    BUILD_COMMAND_PROD: './gradlew clean assembleRelease --no-daemon',

    FIREBASE_CRED: 'ANDROID_CLIENT_GOOGLE',
    FIREBASE_CRED_FILE: 'app/google-services.json',
    FIREBASE_UPLOAD_COMMAND: './gradlew assembleDebug appDistributionUploadDebug',

    SIGNING_CRED: 'android-apk-sign-cred',
    SIGNING_KEY_ALIAS: 'ru.sberbank.sberapps',

    //QG_SQ_COMMAND: './gradlew clean sonarqube',
    //QG_SQ_PROJECTKEY: 'sbermail-android-mail-client',

    //QG_CX_MUS_CODE: '00GH0004',
    //QG_CX_MUS_NAME: 'SberMail',
    //QG_CX_EMAIL: 'Ganza.R.A@sberbank.ru,IVlMakarov@sberbank.ru,RAKavyrshin@sberbank.ru',
    //QG_CX_SM_ID: '2446095',
    //QG_CX_SM_NAME: 'Безопасный почтовый клиент (Android)',
    //QG_CX_JIRA_PROJECT: 'SMA',
    //QG_CX_APP_ID: '14',

    //NEXUS2_REPO: 'Nexus_PROD',
    //NEXUS2_ID: 'CI02446095_SberMail_Android',

    EMAIL_NOTIFY_DEV: 'sakochubievskiy@sberbank.ru',
    EMAIL_NOTIFY_REL: 'sakochubievskiy@sberbank.ru'
)