## アプリの概要
「ファイル管理アプリ」は、業務で使用するファイルの保存と処分をサポートすることを目的としたファイル管理アプリです。<br>
このサービスは、ファイルの保存場所と保存期間を管理し、保存期限年を過ぎたファイルを検索することができファイルの処分を円滑にします。<br>
さらに、ファイルを持出している場合、持出者の情報を確認できファイルが行方不明になることを防ぎます。<br>
「ファイル管理アプリ」は、業務に必要なファイルの所在確認をスムーズにし業務効率を上げるためのサポートを提供します。

## 作成背景
公官庁や企業では業務書類を紙ベースで保存しているところもあり、保存期間を超過した書類は処分します。しかし、ファイルの保存場所が適切に管理されていないこともあり、ファイルが必要になった際にスムーズにファイルを見つけられない、処分時期に適切に処分がされないファイルが保存場所を圧迫するなど問題がありました。<br>
そこでファイルの保存場所と保存期間を管理するAPIを作成しようと思いました。

## 主な使用技術
* Java、Spring Boot
* MySQL
* Docker
* JUnit
* CI（GitHub Actions）
* AWS

## アプリケーション概略図
作成中

## 画面遷移図
https://www.figma.com/community/file/1324311353602501525

## アプリケーション機能一覧
| 項目 | 内容 |
| :----: | :----------------------- |
| ログイン機能 | ・ユーザーはログイン、ログアウトできる |
| 検索機能 | ・ユーザーはファイル名、保存場所、作成年、保存期限年でファイルを検索できる |
| ファイル情報の登録 | ・ユーザーはファイル情報を登録できる |
| ファイル情報の読込 | ・ユーザーはファイル情報を確認できる |
| ファイル情報の更新 | ・ユーザーはファイル情報を更新できる |
| ファイル情報の削除 | ・ユーザーはファイル情報を削除できる　　|
| ファイル分類の登録 | ・管理者ユーザーは新しいファイル分類を登録できる　　|
| ファイル分類の更新 | ・管理者ユーザーはファイル分類を更新できる　　|
| ファイル分類の削除 | ・管理者ユーザーはファイル分類を削除できる<br> (ファイルの参照元になっているファイル分類は削除できない)　　|
| 保存場所の登録 | ・管理者ユーザーは保存場所を登録できる　　|
| 保存場所の更新 | ・管理者ユーザーは保存場所を更新できる　　|
| 保存場所の削除 | ・管理者ユーザーは保存場所を削除できる<br> (ファイルが保存されている保存場所は削除できない)　　|
| ユーザー情報の登録 | ・管理者ユーザーはユーザーを登録できる |
| ユーザー情報の更新 | ・管理者ユーザーはユーザー情報を更新できる |
| ユーザー情報の削除 | ・管理者ユーザーはユーザー情報を削除できる |
| 通知機能 | ・保存期間を超過したファイルがある場合、毎月に管理者にメールで通知される |

## 機能のロジック
### ユーザー登録と削除
![ユーザーロジック](https://github.com/chie-hira/files-management-API/assets/148871501/e430083e-4b30-47e1-ba58-af231258a8f9)

### 保存期間の判定方法
![保存期間ロジック](https://github.com/chie-hira/files-management-API/assets/148871501/58e03720-4e11-4319-994f-33c0b2a0976c)

## API仕様書
https://chie-hira.github.io/files-management-API/dist/index.html

## ER図
![ER](https://github.com/chie-hira/files-management-API/assets/148871501/fb8e9450-4a8d-4bee-a69b-9d79aaa1b80f)

