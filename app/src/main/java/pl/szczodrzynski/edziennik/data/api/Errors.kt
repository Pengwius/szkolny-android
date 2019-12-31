/*
 * Copyright (c) Kuba Szczodrzyński 2019-9-21.
 */

package pl.szczodrzynski.edziennik.data.api

/*const val CODE_OTHER                                        = 0
const val CODE_OK                                           = 1
const val CODE_NO_INTERNET                                  = 10
const val CODE_SSL_ERROR                                    = 13
const val CODE_ARCHIVED                                     = 5
const val CODE_MAINTENANCE                                  = 6
const val CODE_LOGIN_ERROR                                  = 7
const val CODE_ACCOUNT_MISMATCH                             = 8
const val CODE_APP_SERVER_ERROR                             = 9
const val CODE_MULTIACCOUNT_SETUP                           = 12
const val CODE_TIMEOUT                                      = 11
const val CODE_PROFILE_NOT_FOUND                            = 14
const val CODE_ATTACHMENT_NOT_AVAILABLE                     = 28
const val CODE_INVALID_LOGIN                                = 2
const val CODE_INVALID_SERVER_ADDRESS                       = 21
const val CODE_INVALID_SCHOOL_NAME                          = 22
const val CODE_INVALID_DEVICE                               = 23
const val CODE_OLD_PASSWORD                                 = 4
const val CODE_INVALID_TOKEN                                = 24
const val CODE_EXPIRED_TOKEN                                = 27
const val CODE_INVALID_SYMBOL                               = 25
const val CODE_INVALID_PIN                                  = 26
const val CODE_LIBRUS_NOT_ACTIVATED                         = 29
const val CODE_SYNERGIA_NOT_ACTIVATED                       = 32
const val CODE_LIBRUS_DISCONNECTED                          = 31
const val CODE_PROFILE_ARCHIVED                             = 30*/

const val ERROR_APP_CRASH                                   = 1

const val ERROR_REQUEST_FAILURE                             = 50
const val ERROR_REQUEST_HTTP_400                            = 51
const val ERROR_REQUEST_HTTP_401                            = 52
const val ERROR_REQUEST_HTTP_403                            = 53
const val ERROR_REQUEST_HTTP_404                            = 54
const val ERROR_REQUEST_HTTP_405                            = 55
const val ERROR_REQUEST_HTTP_410                            = 56
const val ERROR_REQUEST_HTTP_500                            = 57
const val ERROR_REQUEST_FAILURE_HOSTNAME_NOT_FOUND          = 60
const val ERROR_REQUEST_FAILURE_TIMEOUT                     = 61
const val ERROR_REQUEST_FAILURE_NO_INTERNET                 = 62
const val ERROR_RESPONSE_EMPTY                              = 100
const val ERROR_LOGIN_DATA_MISSING                          = 101
const val ERROR_PROFILE_MISSING                             = 105
const val ERROR_INVALID_LOGIN_MODE                          = 110
const val ERROR_LOGIN_METHOD_NOT_SATISFIED                  = 111
const val ERROR_NOT_IMPLEMENTED                             = 112
const val ERROR_FILE_DOWNLOAD                               = 113

const val ERROR_NO_STUDENTS_IN_ACCOUNT                      = 115

const val CODE_INTERNAL_LIBRUS_ACCOUNT_410                  = 120
const val CODE_INTERNAL_LIBRUS_SYNERGIA_EXPIRED             = 121
const val ERROR_LOGIN_LIBRUS_API_CAPTCHA_NEEDED             = 124
const val ERROR_LOGIN_LIBRUS_API_CONNECTION_PROBLEMS        = 125
const val ERROR_LOGIN_LIBRUS_API_INVALID_CLIENT             = 126
const val ERROR_LOGIN_LIBRUS_API_REG_ACCEPT_NEEDED          = 127
const val ERROR_LOGIN_LIBRUS_API_CHANGE_PASSWORD_ERROR      = 128
const val ERROR_LOGIN_LIBRUS_API_PASSWORD_CHANGE_REQUIRED   = 129
const val ERROR_LOGIN_LIBRUS_API_INVALID_LOGIN              = 130
const val ERROR_LOGIN_LIBRUS_API_OTHER                      = 131
const val ERROR_LOGIN_LIBRUS_PORTAL_CSRF_MISSING            = 132
const val ERROR_LOGIN_LIBRUS_PORTAL_NOT_ACTIVATED           = 133
const val ERROR_LOGIN_LIBRUS_PORTAL_ACTION_ERROR            = 134
const val ERROR_LOGIN_LIBRUS_PORTAL_SYNERGIA_TOKEN_MISSING  = 139
const val ERROR_LIBRUS_API_TOKEN_EXPIRED                    = 140
const val ERROR_LIBRUS_API_INSUFFICIENT_SCOPES              = 141
const val ERROR_LIBRUS_API_OTHER                            = 142
const val ERROR_LIBRUS_API_ACCESS_DENIED                    = 143
const val ERROR_LIBRUS_API_RESOURCE_NOT_FOUND               = 144
const val ERROR_LIBRUS_API_DATA_NOT_FOUND                   = 145
const val ERROR_LIBRUS_API_TIMETABLE_NOT_PUBLIC             = 146
const val ERROR_LIBRUS_API_RESOURCE_ACCESS_DENIED           = 147
const val ERROR_LIBRUS_API_INVALID_REQUEST_PARAMS           = 148
const val ERROR_LIBRUS_API_INCORRECT_ENDPOINT               = 149
const val ERROR_LIBRUS_API_LUCKY_NUMBER_NOT_ACTIVE          = 150
const val ERROR_LIBRUS_API_NOTES_NOT_ACTIVE                 = 151
const val ERROR_LOGIN_LIBRUS_SYNERGIA_NO_TOKEN              = 152
const val ERROR_LOGIN_LIBRUS_SYNERGIA_TOKEN_INVALID         = 153
const val ERROR_LOGIN_LIBRUS_SYNERGIA_NO_SESSION_ID         = 154
const val ERROR_LIBRUS_MESSAGES_ACCESS_DENIED               = 155
const val ERROR_LIBRUS_SYNERGIA_ACCESS_DENIED               = 156
const val ERROR_LOGIN_LIBRUS_MESSAGES_NO_SESSION_ID         = 157
const val ERROR_LIBRUS_PORTAL_ACCESS_DENIED                 = 158
const val ERROR_LIBRUS_PORTAL_API_DISABLED                  = 159
const val ERROR_LIBRUS_PORTAL_SYNERGIA_DISCONNECTED         = 160
const val ERROR_LIBRUS_PORTAL_OTHER                         = 161
const val ERROR_LIBRUS_PORTAL_SYNERGIA_NOT_FOUND            = 162
const val ERROR_LOGIN_LIBRUS_PORTAL_OTHER                   = 163
const val ERROR_LOGIN_LIBRUS_PORTAL_CODE_EXPIRED            = 164
const val ERROR_LOGIN_LIBRUS_PORTAL_CODE_REVOKED            = 165
const val ERROR_LOGIN_LIBRUS_PORTAL_NO_CLIENT_ID            = 166
const val ERROR_LOGIN_LIBRUS_PORTAL_NO_CODE                 = 167
const val ERROR_LOGIN_LIBRUS_PORTAL_NO_REFRESH              = 168
const val ERROR_LOGIN_LIBRUS_PORTAL_NO_REDIRECT             = 169
const val ERROR_LOGIN_LIBRUS_PORTAL_UNSUPPORTED_GRANT       = 170
const val ERROR_LOGIN_LIBRUS_PORTAL_INVALID_CLIENT_ID       = 171
const val ERROR_LOGIN_LIBRUS_PORTAL_REFRESH_INVALID         = 172
const val ERROR_LOGIN_LIBRUS_PORTAL_REFRESH_REVOKED         = 173
const val ERROR_LIBRUS_SYNERGIA_OTHER                       = 174
const val ERROR_LIBRUS_SYNERGIA_MAINTENANCE                 = 175
const val ERROR_LIBRUS_MESSAGES_MAINTENANCE                 = 176
const val ERROR_LIBRUS_MESSAGES_ERROR                       = 177
const val ERROR_LIBRUS_MESSAGES_OTHER                       = 178
const val ERROR_LOGIN_LIBRUS_MESSAGES_INVALID_LOGIN         = 179
const val ERROR_LOGIN_LIBRUS_PORTAL_INVALID_LOGIN           = 180
const val ERROR_LIBRUS_API_MAINTENANCE                      = 181
const val ERROR_LIBRUS_PORTAL_MAINTENANCE                   = 182

const val ERROR_LOGIN_MOBIDZIENNIK_WEB_INVALID_LOGIN        = 201
const val ERROR_LOGIN_MOBIDZIENNIK_WEB_OLD_PASSWORD         = 202
const val ERROR_LOGIN_MOBIDZIENNIK_WEB_INVALID_DEVICE       = 203
const val ERROR_LOGIN_MOBIDZIENNIK_WEB_ARCHIVED             = 204
const val ERROR_LOGIN_MOBIDZIENNIK_WEB_MAINTENANCE          = 205
const val ERROR_LOGIN_MOBIDZIENNIK_WEB_INVALID_ADDRESS      = 206
const val ERROR_LOGIN_MOBIDZIENNIK_WEB_OTHER                = 210
const val ERROR_MOBIDZIENNIK_WEB_ACCESS_DENIED              = 211
const val ERROR_MOBIDZIENNIK_WEB_NO_SESSION_KEY             = 212
const val ERROR_MOBIDZIENNIK_WEB_NO_SESSION_VALUE           = 216
const val ERROR_MOBIDZIENNIK_WEB_NO_SERVER_ID               = 213
const val ERROR_MOBIDZIENNIK_WEB_INVALID_RESPONSE           = 214
const val ERROR_LOGIN_MOBIDZIENNIK_WEB_NO_SESSION_ID        = 215

const val ERROR_LOGIN_VULCAN_INVALID_SYMBOL                 = 301
const val ERROR_LOGIN_VULCAN_INVALID_TOKEN                  = 302
const val ERROR_LOGIN_VULCAN_INVALID_PIN                    = 309
const val ERROR_LOGIN_VULCAN_INVALID_PIN_0_REMAINING        = 310
const val ERROR_LOGIN_VULCAN_INVALID_PIN_1_REMAINING        = 311
const val ERROR_LOGIN_VULCAN_INVALID_PIN_2_REMAINING        = 312
const val ERROR_LOGIN_VULCAN_EXPIRED_TOKEN                  = 321
const val ERROR_LOGIN_VULCAN_OTHER                          = 322
const val ERROR_LOGIN_VULCAN_ONLY_KINDERGARTEN              = 330
const val ERROR_LOGIN_VULCAN_NO_PUPILS                      = 331
const val ERROR_VULCAN_API_MAINTENANCE                      = 340
const val ERROR_VULCAN_API_BAD_REQUEST                      = 341
const val ERROR_VULCAN_API_OTHER                            = 342

const val ERROR_LOGIN_IDZIENNIK_WEB_INVALID_LOGIN           = 401
const val ERROR_LOGIN_IDZIENNIK_WEB_INVALID_SCHOOL_NAME     = 402
const val ERROR_LOGIN_IDZIENNIK_WEB_PASSWORD_CHANGE_NEEDED  = 403
const val ERROR_LOGIN_IDZIENNIK_WEB_MAINTENANCE             = 404
const val ERROR_LOGIN_IDZIENNIK_WEB_SERVER_ERROR            = 405
const val ERROR_LOGIN_IDZIENNIK_WEB_OTHER                   = 410
const val ERROR_LOGIN_IDZIENNIK_WEB_API_NO_ACCESS           = 411 /* {"d":{"__type":"mds.Web.mod_komunikator.WS_mod_wiadomosci+detailWiadomosci","Wiadomosc":{"_recordId":0,"DataNadania":null,"DataOdczytania":null,"Nadawca":null,"ListaOdbiorcow":[],"Tytul":null,"Text":null,"ListaZal":[]},"Bledy":{"__type":"mds.Module.Globalne+sBledy","CzyJestBlad":true,"ListaBledow":["Nie masz dostępu do tych zasobów!"],"ListaKodowBledow":[]},"czyJestWiecej":false}} */
const val ERROR_LOGIN_IDZIENNIK_WEB_NO_SESSION              = 420
const val ERROR_LOGIN_IDZIENNIK_WEB_NO_AUTH                 = 421
const val ERROR_LOGIN_IDZIENNIK_WEB_NO_BEARER               = 422
const val ERROR_IDZIENNIK_WEB_ACCESS_DENIED                 = 430
const val ERROR_IDZIENNIK_WEB_OTHER                         = 431
const val ERROR_IDZIENNIK_WEB_MAINTENANCE                   = 432
const val ERROR_IDZIENNIK_WEB_SERVER_ERROR                  = 433
const val ERROR_IDZIENNIK_WEB_PASSWORD_CHANGE_NEEDED        = 434
const val ERROR_LOGIN_IDZIENNIK_FIRST_NO_SCHOOL_YEAR        = 440
const val ERROR_IDZIENNIK_WEB_REQUEST_NO_DATA               = 441
const val ERROR_IDZIENNIK_API_ACCESS_DENIED                 = 450
const val ERROR_IDZIENNIK_API_OTHER                         = 451

const val ERROR_LOGIN_EDUDZIENNIK_WEB_INVALID_LOGIN         = 501
const val ERROR_LOGIN_EDUDZIENNIK_WEB_OTHER                 = 510
const val ERROR_LOGIN_EDUDZIENNIK_WEB_NO_SESSION_ID         = 511
const val ERROR_EDUDZIENNIK_WEB_TIMETABLE_NOT_PUBLIC        = 520
const val ERROR_EDUDZIENNIK_WEB_LIMITED_ACCESS              = 521
const val ERROR_EDUDZIENNIK_WEB_SESSION_EXPIRED             = 522
const val ERROR_EDUDZIENNIK_WEB_TEAM_MISSING                = 530

const val ERROR_TEMPLATE_WEB_OTHER                          = 801

const val EXCEPTION_API_TASK                                = 900
const val EXCEPTION_LOGIN_LIBRUS_API_TOKEN                  = 901
const val EXCEPTION_LOGIN_LIBRUS_PORTAL_TOKEN               = 902
const val EXCEPTION_LIBRUS_PORTAL_SYNERGIA_TOKEN            = 903
const val EXCEPTION_LIBRUS_API_REQUEST                      = 904
const val EXCEPTION_LIBRUS_SYNERGIA_REQUEST                 = 905
const val EXCEPTION_MOBIDZIENNIK_WEB_REQUEST                = 906
const val EXCEPTION_VULCAN_API_REQUEST                      = 907
const val EXCEPTION_MOBIDZIENNIK_WEB_FILE_REQUEST           = 908
const val EXCEPTION_LIBRUS_MESSAGES_FILE_REQUEST            = 909
const val EXCEPTION_NOTIFY                                  = 910
const val EXCEPTION_LIBRUS_MESSAGES_REQUEST                 = 911
const val EXCEPTION_IDZIENNIK_WEB_REQUEST                   = 912
const val EXCEPTION_IDZIENNIK_WEB_API_REQUEST               = 913
const val EXCEPTION_IDZIENNIK_API_REQUEST                   = 914
const val EXCEPTION_EDUDZIENNIK_WEB_REQUEST                 = 920
const val EXCEPTION_EDUDZIENNIK_FILE_REQUEST                = 921

const val LOGIN_NO_ARGUMENTS                                = 1201
