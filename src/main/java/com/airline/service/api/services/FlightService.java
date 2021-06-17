package com.airline.service.api.services;

import java.sql.Date;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.airline.service.api.entities.Airline;
import com.airline.service.api.entities.City;
import com.airline.service.api.entities.Flight;
import com.airline.service.api.exceptions.FlightNotFoundException;
import com.airline.service.api.repos.AirlineRepository;
import com.airline.service.api.repos.CityRepository;
import com.airline.service.api.repos.FlightRepository;

@Service
public class FlightService {

	private static Logger LOGGER = LoggerFactory.getLogger(FlightService.class.getName());

	@Autowired
	private FlightRepository flightRepository;
	@Autowired 
	private CityRepository cityRepository;
	@Autowired
	private AirlineRepository airlineRepository;

	public Flight getFlightById(String id) {
		LOGGER.info("***getFlightById() invoked***");
		Flight flight = flightRepository.getById(id);
		if (flight == null) {
			LOGGER.info("***No flight record for id::" + id + "***");
			throw new FlightNotFoundException(id);
		} else {
			return flight;
		}
	}
	
	// instance of getFlight to return flightInfo object instead of flight object
	public FlightInfo getFlightByIdRest(String id) {
		LOGGER.info("***getFlightById() invoked***");
		Flight flight = flightRepository.getById(id);
		if (flight == null) {
			LOGGER.info("***No flight record for id::" + id + "***");
			throw new FlightNotFoundException(id);
		} else {
			// Packaged objects for REST API
			Airline airline = airlineRepository.findByAirlineID(flight.getAirlineID());
			City destCity = cityRepository.findByCityID(flight.getDestCity());
			City originCity = cityRepository.findByCityID(flight.getOriginCity());
			return new FlightInfo(flight, airline, originCity, destCity);
		}

	}

	public List<Flight> getAllFlights() {
		LOGGER.info("***getAllFlight() invoked***");
		List<Flight> flights = flightRepository.findAll();
		if (flights == null || flights.size() == 0) {
			LOGGER.info("***No flights***");
			throw new FlightNotFoundException();
		} else {
			return flights;
		}

	}

	public void saveFlightToDB(Flight flight) {
		if (flight != null) {
			flightRepository.save(flight);
		} else {
			throw new RuntimeException("No flight records to save");
		}
	}

	public List<Flight> searchFlightRecords(Flight flight, String flightType) {

		LOGGER.info("***searchFlightRecords invoked***");
		Date leaveDate=flight.getLeaveDate(), arriveDate=flight.getArriveDate();
		int seatsAvailable=flight.getSeatsAvailable(), originCity=flight.getOriginCity(), destCity=flight.getDestCity();
		List<Flight> flightResult = null;
		if(flightType!=null) {
			int price=Integer.valueOf(flightType);
			 flightResult=flightRepository.searchFlights(leaveDate, arriveDate, seatsAvailable, originCity, destCity);
			 for (Flight temp : flightResult) {
				 temp.setPrice(temp.getPrice()*price);
		            
		        }
		}
		return flightResult; 
	}

	public Flight flightToBookById(String id) {
		
		return flightRepository.getById(id);
	}
}
