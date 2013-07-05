//#charset: windows-1252

VERSION "4.0"

WINDOW 101 "Generate Classes" 100 100 424 584
BEGIN
    EVENTMASK 3287287500
    INVISIBLE
    KEYBOARDNAVIGATION
    NAME "Form"
    LISTBOX 206, "", 118, 101, 195, 210
    BEGIN
        CLIENTEDGE
        MULTISELECT
        NAME "LB_TABLES"
    END

    STATICTEXT 200, "Database:", 14, 24, 86, 13
    BEGIN
        JUSTIFICATION 32768
        NAME "ST_DB"
        NOT OPAQUE
        NOT WORDWRAP
    END

    BUTTON 203, "Refresh", 324, 100, 90, 23
    BEGIN
        DISABLED
        NAME "BTN_REFRESH"
    END

    LISTEDIT 201, "", 118, 21, 195, 250
    BEGIN
        NAME "LE_DB"
        SELECTIONHEIGHT 21
    END

    STATICTEXT 205, "Tables:", 14, 102, 86, 13
    BEGIN
        JUSTIFICATION 32768
        NAME "ST_TABLES"
        NOT OPAQUE
        NOT WORDWRAP
    END

    STATICTEXT 210, "Target Dir:", 14, 372, 86, 13
    BEGIN
        JUSTIFICATION 32768
        NAME "ST_DIR"
        NOT OPAQUE
        NOT WORDWRAP
    END

    EDIT 220, "", 118, 367, 195, 21
    BEGIN
        CLIENTEDGE
        MAXLENGTH 255
        NAME "ED_TARGETDIR"
    END

    BUTTON 300, "Generate Classes", 142, 501, 139, 27
    BEGIN
        NAME "BTN_GENERATE"
    END

    STATICTEXT 100, "User/Password:", 14, 52, 86, 13
    BEGIN
        JUSTIFICATION 32768
        NAME "ST_CRED"
        NOT OPAQUE
        NOT WORDWRAP
    END

    EDIT 101, "", 118, 48, 93, 21
    BEGIN
        CLIENTEDGE
        MAXLENGTH 128
        NAME "ED_USER"
    END

    EDIT 102, "", 220, 48, 93, 21
    BEGIN
        CLIENTEDGE
        MAXLENGTH 128
        NAME "ED_PWD"
        PASSWORDENTRY
        NOT DRAGENABLED
    END

    STATICTEXT 103, "Schema:", 14, 77, 86, 13
    BEGIN
        JUSTIFICATION 32768
        NAME "ST_SCHEMA"
        NOT OPAQUE
        NOT WORDWRAP
    END

    EDIT 104, "", 118, 74, 195, 21
    BEGIN
        CLIENTEDGE
        MAXLENGTH 128
        NAME "ED_SCHEMA"
    END

    TOOLBUTTON 105, "", 324, 367, 26, 21
    BEGIN
        NAME "TB_FIND"
        SHORTCUE "Lookup directory"
        DROPACTIONS 0
    END

    TOOLBUTTON 106, "", 324, 320, 26, 21
    BEGIN
        NAME "TB_FINDTPLDIR"
        SHORTCUE "Lookup directory"
        DROPACTIONS 0
    END

    STATICTEXT 107, "Template Dir:", 15, 325, 86, 13
    BEGIN
        JUSTIFICATION 32768
        NAME "ST_TPL_DIR"
        NOT OPAQUE
        NOT WORDWRAP
    END

    EDIT 108, "", 118, 320, 195, 21
    BEGIN
        CLIENTEDGE
        MAXLENGTH 255
        NAME "ED_TPLDIR"
    END

    CHECKBOX 109, "Prepend Database Name to Class Names", 117, 400, 232, 25
    BEGIN
        CHECKED
        NAME "CB_PREPENDDB"
        NOT OPAQUE
    END

    PROGRESSBAR 110, 16, 548, 391, 21
    BEGIN
        CUSTOMTEXT ""
        INVISIBLE
        NAME "BBj Progress Bar"
    END

    STATICTEXT 111, "", 16, 552, 391, 20
    BEGIN
        JUSTIFICATION 16384
        NAME "ST_MSG"
        NOT OPAQUE
    END

    CHECKBOX 112, "Overwrite all files.", 117, 458, 232, 25
    BEGIN
        NAME "CB_OVERWRITE"
        NOT OPAQUE
    END

END

