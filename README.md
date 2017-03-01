# play-slick-restful-api-seed

Macへの環境の作り方

## ScalaとPlayframeworkをインストールする

Java8がインストールされていることを確認し、ScalaとActivatorを入れる 

$ java -version 

$ brew install scala 

$ brew install typesafe-activator 

Githubより、play-slick-api-seed のソースをダウンロード https://github.com/asaas/play-slick-restful-api-seed し、解凍する

$ cd play-slick-restful-api-seed 

$ activator run 

## データベース

$ brew install mysql56 

$ mysql.server start

localのMySQLに、データベース api_seed を作成
conf/evolutions.defaut/1.sql を適用

## テスター

automutton-api-tester

https://github.com/asaas/automutton-api-tester をダウンロード

$ cd automutton-api-tester

$ activator

$ run -Dhttp.port=8999

ブラウザで http://localhost:8999

その他、curlでもテスト可能

## IntelliJ

Import Project > ~/play-slick-restful-api-seed/build.sbt を選択する
