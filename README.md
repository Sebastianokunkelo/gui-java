# appleJuice Client GUI

![](https://img.shields.io/github/release/applejuicenet/gui-java.svg)
![](https://github.com/applejuicenet/gui-java/workflows/release/badge.svg)
![](https://img.shields.io/github/license/applejuicenet/gui-java.svg)

Dieses GUI ist das grafisches Interface (Graphical User Interface) für den appleJuice Core.

## Installation

### Windows

Bitte das Setup von [hier](https://github.com/applejuicenet/setup/releases) oder die `AJCoreGUI.zip` von [hier](https://github.com/applejuicenet/gui-java/releases) nehmen.

### macOS

- per [Homebrew](https://brew.sh) installieren `brew cask install applejuicenet/packages/applejuice-gui`
- oder die `AJCoreGUI.masOS.zip` von [hier](https://github.com/applejuicenet/gui-java/releases) nehmen

### Linux

Für Linux wurde eine `snap` Paket zusammengestellt.

[![Installieren vom Snap Store](https://snapcraft.io/static/images/badges/de/snap-store-white.svg)](https://snapcraft.io/applejuice-gui)

## Themes

Das GUI hat einen Theme Support, weitere Themes gibts [hier](https://github.com/l2fprod/javootoo.com/tree/master/plaf/skinlf/themepacks)

## mehr Informationen Button

Der `Suche nach mehr Informationen` Button im Kontextmenü öffnet eine URL, mit dem dahinterliegenden `ajfsp` Link als GET Parameter.

Dafür muss im `~/appleJuice/gui/` Ordner eine `rel.properties` Datei mit folgender Konfiguration vorhanden sein (wird automatisch angelegt):

```ini
host=https://relinfo.tld/api/ajfsp/?link=%s
```

Das letzte `%s` wird mit dem vollständigen `ajfsp` Link ersetzt (urlencoded).

### neues Release in dieser Reihenfolge erstellen

1. Version in `de.applejuicenet.client.gui.AppleJuiceDialog.GUI_VERSION` anpassen
2. Changelog anpassen
3. Änderungen committen und taggen
4. Warten bis die Github Release Action durchgelaufen ist
5. in der `snapcraft.yaml` Die Version anheben (darf wirklich erst nach dem erstellen des Releases passieren)
6. Änderungen der `snapcraft.yaml` commiten (ohne Taggen) > triggert dann einen build für http://snapcraft.io/applejuice-gui/builds

