select persons.weight as weight, persons.hair_style as hair_style, persons.fingerprints_available as fingerprints_available, 
persons.build as build, persons.creator_id as user_entry, persons.complete_name as complete_name, 
persons.country_citizen as country_citizen, persons.given_name1 as given_name1, persons.given_name2 as given_name2, 
persons.religion as religion, persons.date_deceased as deceased, persons.height as height, 
persons.place_of_birth as place_of_birth, persons.marital_status as marital_status, persons.dob as dob, 
persons.last_name as last_name, persons.country_birth as country_birth, persons.dna_available as dna_available, 
persons.gender as gender, persons.race as race, persons.citizenship_status as citizenship_status, 
persons.colour_hair as colour_hair, persons.person_id as id, persons.person_id as promis3_persons, 
persons.colour_eyes as colour_eyes, persons.date_created as date_created, persons.complxn as compllxn from persons


select * from persons as p, object_links as ol, object_links as ol2, cases as c, locations as l
where p.id = ol.from_obj_id and ol.from_obj_type_code='per' 
  and c.case_id = ol.to_obj_id and ol.to_obj_type_code='cse'
  and p.id = ol2.from_obj_id and ol2.from_obj_type_code='per'
  and l.id = ol2.to_obj_id and ol2.to_obj_type_code='loc';
  
  
select distinct * from persons
left join persons_cases on (persons.person_id = persons_cases.persons_id) 
left join cases on (persons_cases.case_id = cases.case_id) 
left join persons_locations on (persons.person_id = persons_locations.persons_id) 
left join locations on (persons_locations.location_id = locations.location_id) 
left join persons_contact_numbers on (persons.person_id = persons_contact_numbers.persons_id) 
left join contact_numbers on (persons_contact_numbers.contact_id = contact_numbers.contact_id)

select distinct ol1.from_obj_id as id from persons 
left join object_links as ol1 on (persons.id = ol1.from_obj_id)
left join locations on (ol1.to_obj_id = locations.id) 
left join object_links as ol2 on (persons.id = ol2.from_obj_id)