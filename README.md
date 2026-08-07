# IME Slash Helper

Minecraft Fabric クライアント MOD。

- 日本語入力（IME ON）のままチャットを開いて `/` を押すと
  - 全角の `／` / `・` を **半角 `/`** に変換して入力
  - IME を **OFF（直接入力）** に切り替え
- **Enter** を押してチャットを閉じると、元の **日本語入力に自動復帰**

Windows 専用（Win32 IMM32 API を JNA 経由で使用）。macOS / Linux では何もせず安全に無効化されます。

## 対応バージョン

| グループ | Minecraft | Java | Gradle | Loom |
| --- | --- | --- | --- | --- |
| `legacy/` | 1.20.1 / 1.20.4 / 1.20.6 / 1.21.1 / 1.21.4 / 1.21.8 / 1.21.11 | 17～21 | 8.12 | 1.11 |
| `modern/` | 26.1.2 | 25 | 9.4 | 1.17 |

ソースは `src/` の **1 つだけ**。Stonecutter のプリプロセスコメントでバージョン差分を吸収します。

## ディレクトリ構成

```
ime-slash-helper/
├── src/main/            ← 共通ソース（これ 1 つだけ）
├── dist/                ← 全バージョンの jar 集約先
├── build-all.sh / build-all.ps1
├── legacy/              ← 1.20.x + 1.21.x
│   └── versions/<MC>/gradle.properties
└── modern/              ← 26.x
    └── versions/26.1.2/gradle.properties
```

## 初回セットアップ（ローカルでビルドする場合のみ）

Gradle Wrapper のバイナリ（`gradlew` / `gradle-wrapper.jar`）はこの ZIP には含めていません。
ローカルで使う場合は、Gradle を入れて 1 回だけ以下を実行してください（`gradle-wrapper.properties` は同梱済みなのでバージョンは自動で合います）。

```bash
cd legacy && gradle wrapper && cd ..
cd modern && gradle wrapper && cd ..
```

**GitHub Actions だけでビルドするならこの手順は不要です**（CI 側で Gradle を直接セットアップします）。

## 一括ビルド

```bash
./build-all.sh            # macOS / Linux
powershell -File build-all.ps1   # Windows
```

全バージョンの jar が `dist/` に集まります。

## 個別コマンド

| やりたいこと | コマンド |
| --- | --- |
| 1.20/1.21 全部 | `cd legacy && ./gradlew collectJars` |
| 26.x | `cd modern && ./gradlew collectJars` |
| 1 バージョンに切り替え | `./gradlew "Set active project to 1.20.1"` |
| 切り替えを戻す | `./gradlew "Reset active project"` |
| デバッグ起動 | `./gradlew runClient` |

> **重要**: `legacy` と `modern` を**同時並行で実行しないでください**。Stonecutter は共通 `src/` を書き換えながらビルドするため、同時実行すると壊れます。

## 注意 / 要検証項目

- `ChatScreen#charTyped` のシグネチャがイベントオブジェクト式になる境界を `//? if <1.21.9` としています。実際の境界バージョンは手元の mapping で確認して調整してください。
- `loader_version` / `loom_version` / Stonecutter のバージョンは https://fabricmc.net/develop で最新値を確認してください。
