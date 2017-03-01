package api

import javax.inject.Inject

import api.ApiUtil._
import play.api.libs.json._

/*
* Successful response for an ApiRequest.
*/
case class ApiResponse @Inject()(status: Int, json: JsValue, headers: Seq[(String, String)]) extends ApiResult {

}

object ApiResponse {

  //////////////////////////////////////////////////////////////////////
  // Status Codes

  final val STATUS_OK = 200
  final val STATUS_CREATED = 201
  final val STATUS_ACCEPTED = 202
  final val STATUS_NOCONTENT = 204
  final val STATUS_REDIRECT = 303

  //////////////////////////////////////////////////////////////////////
  // Predefined responses

  def ok(json: JsValue, headers: (String, String)*) = apply(STATUS_OK, json, headers)

  def created(json: JsValue, headers: (String, String)*) = apply(STATUS_CREATED, json, headers)

  def created(headers: (String, String)*) = apply(STATUS_CREATED, JsNull, headers)

  def accepted(json: JsValue, headers: (String, String)*) = apply(STATUS_ACCEPTED, json, headers)

  def accepted(headers: (String, String)*) = apply(STATUS_ACCEPTED, JsNull, headers)

  def noContent(headers: (String, String)*) = apply(STATUS_NOCONTENT, JsNull, headers)

  def redirect(json: JsValue,headers: (String, String)*) = apply(STATUS_REDIRECT, json, headers)

}