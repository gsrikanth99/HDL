package com.hologic.hdl.util

/**
 * This class defines Error XML dataset
 */
case class ErrorRecord (
processid: String,
domain: String,
xml: String
)