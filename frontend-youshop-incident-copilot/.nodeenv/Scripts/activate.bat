
@echo off
if defined _OLD_VIRTUAL_NPM_CONFIG_PREFIX (
    set "npm_config_prefix=%_OLD_VIRTUAL_NPM_CONFIG_PREFIX%"
) else if defined NODE_VIRTUAL_ENV (
    set npm_config_prefix=
)
set "NODE_VIRTUAL_ENV=C:\Users\lucas\OneDrive\Área de Trabalho\YouShop\frontend-youshop-incident-copilot\.nodeenv"
if not defined PROMPT (
    set "PROMPT=$P$G"
)
if defined _OLD_VIRTUAL_PROMPT (
    set "PROMPT=%_OLD_VIRTUAL_PROMPT%"
)
if defined _OLD_VIRTUAL_NODE_PATH (
    set "NODE_PATH=%_OLD_VIRTUAL_NODE_PATH%"
)
set "_OLD_VIRTUAL_PROMPT=%PROMPT%"
set "PROMPT=(.nodeenv) %PROMPT%"
if defined NODE_PATH (
    set "_OLD_VIRTUAL_NODE_PATH=%NODE_PATH%"
    set NODE_PATH=
)
if defined _OLD_VIRTUAL_PATH (
    set "PATH=%_OLD_VIRTUAL_PATH%"
) else (
    set "_OLD_VIRTUAL_PATH=%PATH%"
)
set "PATH=%NODE_VIRTUAL_ENV%\Scripts;%PATH%"
rem npm.cmd runs the npm found under npm_config_prefix, which npx exports,
rem instead of its own: point it at the environment
rem https://github.com/ekalinin/nodeenv/issues/309
if defined npm_config_prefix (
    set "_OLD_VIRTUAL_NPM_CONFIG_PREFIX=%npm_config_prefix%"
)
set "npm_config_prefix=%NODE_VIRTUAL_ENV%\Scripts"
:END

