package dev.luzifer.model;

public class SpicetifyCommands {
  public static final String UPDATE = "spicetify update";
  public static final String APPLY = "spicetify apply";
  public static final String THEME = "spicetify config current_theme ";
  public static final String BACKUP_APPLY = "spicetify backup apply";
  public static final String WINDOWS_INSTALL =
      "iwr -useb https://raw.githubusercontent.com/spicetify/spicetify-cli/master/install.ps1 | iex";
  public static final String WINDOWS_INSTALL_MARKETPLACE =
      "iwr -useb https://raw.githubusercontent.com/spicetify/spicetify-marketplace/main/resources/install.ps1 | iex";
  public static final String OTHER_INSTALL =
      "curl -fsSL https://raw.githubusercontent.com/spicetify/spicetify-cli/master/install.sh | sh";
  public static final String OTHER_INSTALL_MARKETPLACE =
      "curl -fsSL https://raw.githubusercontent.com/spicetify/spicetify-marketplace/main/resources/install.sh | sh";
  public static final String SPICEITFY_RESTORE = "spicetify restore";
  public static final String DELETE_APPDATA_SPICETIFY_WINDOWS =
      "rmdir -r -fo $env:APPDATA\\spicetify";
  public static final String DELETE_LOCALAPPDATA_SPICETIFY_WINDOWS =
      "rmdir -r -fo $env:LOCALAPPDATA\\spicetify";
  public static final String DELETE_APPDATA_SPICETIFY_OTHER = "rm -rf ~/.spicetify";
  public static final String DELETE_LOCALAPPDATA_SPICETIFY_OTHER = "rm -rf ~/.config/spicetify";
}
