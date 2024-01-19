/*
 * Copyright (c) 2022-2022 the original author or authors.
 *
 * MIT License
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and
 * associated documentation files (the "Software"), to deal in the Software without restriction,
 * including without limitation the rights to use, copy, modify, merge, publish, distribute,
 * sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or
 * substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT
 * NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
 * DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */
package com.user.UserCommandModule.auth.api;

import static java.lang.String.format;
import static java.util.stream.Collectors.joining;

import java.io.IOException;
import java.net.URISyntaxException;
import java.time.Instant;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import com.user.UserCommandModule.AMQP.AMQPSender;
import com.user.UserCommandModule.usermanagement.api.CreateCustomerRequest;
import com.user.UserCommandModule.usermanagement.api.CreateUserRequest;
import com.user.UserCommandModule.usermanagement.api.UserView;
import com.user.UserCommandModule.usermanagement.api.UserViewMapper;
import com.user.UserCommandModule.usermanagement.model.User;
import com.user.UserCommandModule.usermanagement.services.UserService;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.web.bind.annotation.*;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

@Tag(name = "Validation", description = "Endpoints for user login and regioster")
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "api")
public class AuthApi {

	private final AuthenticationManager authenticationManager;

	private final JwtEncoder jwtEncoder;

	private final UserViewMapper userViewMapper;

	private final UserService userService;

	private final AMQPSender sender;

	@Operation(summary = "A User Logs In")
	@PostMapping("/login")
	public ResponseEntity<UserView> login(@RequestBody @Valid final AuthRequest request){

			final Authentication authentication = authenticationManager.authenticate(
					new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword()));
			// if the authentication is successful, Spring will store the authenticated user
			// in its "principal"
			final User user = (User) authentication.getPrincipal();

			final Instant now = Instant.now();
			final long expiry = 36000L;

			final String scope = authentication.getAuthorities().stream().map(GrantedAuthority::getAuthority)
					.collect(joining(" "));

			final JwtClaimsSet claims = JwtClaimsSet.builder().issuer("example.io").issuedAt(now)
					.expiresAt(now.plusSeconds(expiry)).subject(format("%s,%s", user.getId(), user.getUsername()))
					.claim("roles", scope).build();

			final String token = this.jwtEncoder.encode(JwtEncoderParameters.from(claims)).getTokenValue();

			return ResponseEntity.ok().eTag(Long.toString(user.getVersion())).header(HttpHeaders.AUTHORIZATION, token).body(userViewMapper.toUserView(user));

	}

	@Operation(summary = "Registers a User with any admin permissions")
	@PostMapping("/register/admin")
	public ResponseEntity<UserView> registerAdmin(@RequestBody @Valid final CreateUserRequest request){
		final var user = userService.createAdmin(request);

		final var newuserUri = ServletUriComponentsBuilder.fromCurrentRequestUri().pathSegment(user.getId().toString())
				.build().toUri();
		sender.sendCreateUser(user);
		return ResponseEntity.created(newuserUri).eTag(Long.toString(user.getVersion())).body(userViewMapper.toUserView(user));
	}

	@Operation(summary = "Registers a new Customer without any permissions")
	@PostMapping("/register")
	public ResponseEntity<UserView> registerUser(@RequestBody @Valid final CreateCustomerRequest request){
		final var user = userService.createCustomer(request);

		final var newuserUri = ServletUriComponentsBuilder.fromCurrentRequestUri().pathSegment(user.getId().toString())
				.build().toUri();
		sender.sendCreateUser(user);
		return ResponseEntity.created(newuserUri).eTag(Long.toString(user.getVersion())).body(userViewMapper.toUserView(user));
	}



}
