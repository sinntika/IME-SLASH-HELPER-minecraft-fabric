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

## 注意 / 要検証項目

- `ChatScreen#charTyped` のシグネチャがイベントオブジェクト式になる境界を `//? if <1.21.9` としています。実際の境界バージョンは手元の mapping で確認して調整してください。
- `loader_version` / `loom_version` / Stonecutter のバージョンは https://fabricmc.net/develop で最新値を確認してください。
