@echo off
if defined _OLD_VIRTUAL_PROMPT (
    set "PROMPT=%_OLD_VIRTUAL_PROMPT%"
)
set _OLD_VIRTUAL_PROMPT=
if defined _OLD_VIRTUAL_NODE_PATH (
    set "NODE_PATH=%_OLD_VIRTUAL_NODE_PATH%"
    set _OLD_VIRTUAL_NODE_PATH=
)
if defined _OLD_VIRTUAL_PATH (
    set "PATH=%_OLD_VIRTUAL_PATH%"
)
set _OLD_VIRTUAL_PATH=
if defined _OLD_VIRTUAL_NPM_CONFIG_PREFIX (
    set "npm_config_prefix=%_OLD_VIRTUAL_NPM_CONFIG_PREFIX%"
    set _OLD_VIRTUAL_NPM_CONFIG_PREFIX=
) else if defined NODE_VIRTUAL_ENV (
    set npm_config_prefix=
)
set NODE_VIRTUAL_ENV=
:END
