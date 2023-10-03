package utils.errors

class NotFoundError(field: String) : ValidationError(field to "Not found.") {
}