// Database Schema for "Kelola Sampah" System

Enum user_role {
  admin
  seller
  courier
}

Enum status_order {
  pending
  picked_up
  completed
  canceled
}

Enum status_courier {
  available
  offline
  on_delivery
  unavailable
}

// ----------------------------
// Tabel Utama
// ----------------------------

Table User as U {
  id int [pk, increment]
  name varchar
  username varchar [unique]
  password varchar
  token varchar
  role user_role
}

Table Contact as CT {
  id int [pk, increment]
  user_id int [ref: > U.id, unique]
  first_name varchar
  last_name varchar
  phone_number varchar
  email varchar
  address varchar
}

Table Admin as A {
  id int [pk, increment]
  user_id int [ref: > U.id, unique]
}

Table Seller as S {
  id int [pk, increment]
  user_id int [ref: > U.id, unique]
  balance double
}

Table Courier as C {
  id int [pk, increment]
  user_id int [ref: > U.id, unique]
  hire_by int [ref: > A.id]
  max_visits_a_day int
  age int
  driving_licence varchar
  id_card varchar
  status status_courier
  successful_deliveries int
  failed_deliveries int
}

Table CourierDailyStats as CDS {
  id int [pk, increment]
  courier_id int [ref: > C.id]
  date date
  visits int
}

Table Trash as T {
  id int [pk, increment]
  address varchar
  photo_proof varchar
  trash_weight double
}

Table TrashOrder as TO {
  id int [pk, increment]
  trash_id int [ref: > T.id]
  seller_id int [ref: > S.id]
  courier_id int [ref: > C.id, null]
  time datetime
  status status_order
}