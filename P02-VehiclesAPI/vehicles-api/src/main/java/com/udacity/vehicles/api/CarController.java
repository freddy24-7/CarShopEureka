package com.udacity.vehicles.api;

import com.udacity.vehicles.domain.car.Car;
import com.udacity.vehicles.service.CarService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.Resource;
import org.springframework.hateoas.Resources;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;
import static org.springframework.http.ResponseEntity.noContent;

/**
 * Implements a REST-based controller for the Vehicles API.
 */
@RestController
@RequestMapping("/cars")
class CarController {

    @Autowired
    private final CarService carService;
    private final CarResourceAssembler assembler;

    public CarController(CarService carService, CarResourceAssembler assembler) {
        this.carService = carService;
        this.assembler = assembler;
    }

    /**
     * Creates a list to store any vehicles.
     * @return list of vehicles
     */
    @GetMapping
    @Operation(summary = "This API retrieves a list of all cars.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    description = "List of cars retrieved successfully",
                    content = {@Content(mediaType = "application/json")}),
            @ApiResponse(responseCode = "500",
                    description = "Internal server error")
    })
    Resources<Resource<Car>> list() {
        List<Resource<Car>> resources = carService.list().stream().map(assembler::toResource)
                .collect(Collectors.toList());
        return new Resources<>(resources,
                linkTo(methodOn(CarController.class).list()).withSelfRel());
    }

    /**
     * Gets information of a specific car by ID.
     * @param id the id number of the given vehicle
     * @return all information for the requested vehicle
     */
    /**
     * IMPLEMENTED: Use the `findById` method from the Car Service to get car information.
     * IMPLEMENTED: Use the `assembler` on that car and return the resulting output.
     *   Update the first line as part of the above implementing.
     */

    @GetMapping("/{id}")
    @Operation(summary = "This API retrieves a specific car "
            + "based on the id of the car.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    description = "car details retrieved successfully",
                    content = {@Content(mediaType = "application/json")}),
            @ApiResponse(responseCode = "404",
                    description =
                            "Car not found with id:"),
            @ApiResponse(responseCode = "500",
                    description = "Internal server error")
    })
    Resource<Car> get(@PathVariable Long id) {
        //Using the findById method from the Car Service to get car information
        Car car = carService.findById(id);

        //Using the assembler on that car and return the resulting output
        return assembler.toResource(car);
    }

    /**
     * Posts information to create a new vehicle in the system.
     * @param car A new vehicle to add to the system.
     * @return response that the new vehicle was added to the system
     * @throws URISyntaxException if the request contains invalid fields or syntax
     */
    /**
     * IMPLEMENTED: Use the `save` method from the Car Service to save the input car.
     * IMPLEMENTED: Use the `assembler` on that saved car and return as part of the response.
     *   Update the first line as part of the above implementing.
     */

    @PostMapping
    @Operation(summary = "This API adds a new car")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201",
                    description = "Car object created successfully",
                    content = {@Content(mediaType = "application/json")}),
            @ApiResponse(responseCode = "500",
                    description = "Internal server error")
    })
    ResponseEntity<Resource<Car>> post(@Valid @RequestBody Car car) throws URISyntaxException {
        //Using the save method from the Car Service to save the input car
        Car savedCar = carService.save(car);

        //Using the assembler on the saved car and return as part of the response
        Resource<Car> resource = assembler.toResource(savedCar);

        //Creating a location URI for the newly created resource
        URI location = new URI(resource.getId().expand().getHref());

        //Returning a ResponseEntity with CREATED status and location header
        return ResponseEntity.created(location).body(resource);
    }

    /**
     *  Updates the information of a vehicle in the system.
     *  @param id The ID number for which to update vehicle information.
     *  @param car The updated information about the related vehicle.
     *  @return response that the vehicle was updated in the system
     * IMPLEMENTED: Set the id of the input car object to the `id` input.
     * IMPLEMENTED: Save the car using the `save` method from the Car service
     * IMPLEMENTED: Use the `assembler` on that updated car and return as part of the response.
     *   Update the first line as part of the above implementing.
     */

    @PutMapping("/{id}")
    @Operation(summary = "This API updates the details of a car")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    description = "Car details successfully updated",
                    content = {@Content(mediaType = "application/json")}),
            @ApiResponse(responseCode = "404",
                    description = "No car object found for that id"),
            @ApiResponse(responseCode = "500",
                    description = "Internal server error")
    })
    ResponseEntity<?> put(@PathVariable Long id, @Valid @RequestBody Car car) {

        car.setId(id);
        Car newCar = this.carService.save(car);
        Resource<Car> resource = assembler.toResource(newCar);
        return ResponseEntity.ok(resource);
    }

    /**
     * Removes a vehicle from the system.
     * @param id The ID number of the vehicle to remove.
     * @return response that the related vehicle is no longer in the system
     */
    /**
     * IMPLEMENTED: Use the Car Service to delete the requested vehicle.
     */

    @DeleteMapping("/{id}")
    @Operation(summary = "This API allows the deletion of a car object")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204",
                    description = "Car object successfully deleted",
                    content = {@Content(mediaType = "application/json")}),
            @ApiResponse(responseCode = "404",
                    description = "No car object found for that id"),
            @ApiResponse(responseCode = "500",
                    description = "Internal server error")
    })
    ResponseEntity<?> delete(@PathVariable Long id) {
        // Use the Car Service to delete the requested vehicle
        carService.delete(id);

        // Return a ResponseEntity with NO_CONTENT status
        return noContent().build();
    }

}
